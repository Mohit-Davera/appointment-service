package com.simformsolutions.doctorappointmentsystem.service;

import com.simformsolutions.doctorappointmentsystem.converter.AppointmentDoctorDtoConverter;
import com.simformsolutions.doctorappointmentsystem.dto.AppointmentDoctorDto;
import com.simformsolutions.doctorappointmentsystem.enums.AppointmentStatus;
import com.simformsolutions.doctorappointmentsystem.excepetionhandler.*;
import com.simformsolutions.doctorappointmentsystem.model.Appointment;
import com.simformsolutions.doctorappointmentsystem.model.Doctor;
import com.simformsolutions.doctorappointmentsystem.model.Schedule;
import com.simformsolutions.doctorappointmentsystem.model.User;
import com.simformsolutions.doctorappointmentsystem.projection.DoctorInter;
import com.simformsolutions.doctorappointmentsystem.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Service
public class AppointmentService {

    @Autowired
    private final ScheduleRepository scheduleRepository;
    @Autowired
    private final DoctorRepository doctorRepository;
    @Autowired
    private final  UserRepository userRepository;
    @Autowired
    private final AppointmentDoctorDtoConverter appointmentDoctorDtoConverter;
    @Autowired
    private final SpecialityRepository specialityRepository;
    @Autowired
    private final AppointmentRepository appointmentRepository;

    ArrayList<AppointmentDoctorDto> globalAvailableDoctors = new ArrayList<>();

    public AppointmentService(ScheduleRepository scheduleRepository, DoctorRepository doctorRepository, UserRepository userRepository, AppointmentDoctorDtoConverter appointmentDoctorDtoConverter, SpecialityRepository specialityRepository,AppointmentRepository appointmentRepository) {
        this.scheduleRepository = scheduleRepository;
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.appointmentDoctorDtoConverter = appointmentDoctorDtoConverter;
        this.specialityRepository = specialityRepository;
        this.appointmentRepository = appointmentRepository;

    }

    public AppointmentDoctorDto checkSchedule(ArrayList<Doctor> doctors, Appointment userAppointment) {

        boolean futureAppointment;
        LocalTime currentTime = getCurrentLocalTime();

        //Predicates
        Predicate<Appointment> sameDateFilter = appointment -> appointment.getDate().equals(userAppointment.getDate());
        Predicate<Appointment> statusFilter = appointment -> appointment.getStatus().equals(AppointmentStatus.BOOKED);
        Predicate<Doctor> appointmentNullFilter = doctor -> doctor.getAppointments().size() == 0;


        //Add Doctor Who Don't Have Any Appointments
        ArrayList<Doctor> freeDoctors = new ArrayList<>(doctors.stream().filter(appointmentNullFilter).toList());
        List<AppointmentDoctorDto> availableDoctors = new ArrayList<>(appointmentDoctorDtoConverter.freeDoctorToBookedDoctorConverter(freeDoctors, userAppointment,currentTime));

        if (freeDoctors.size() != 0) {
            doctors.removeAll(freeDoctors);
        }
        if (doctors.size() != 0) {

            AppointmentDoctorDto appointmentDoctorDto;
            LocalTime doctorBookedTill;

            for (Doctor d : doctors) {
                Optional<LocalTime> optionalDoctorBookedTill = d.getAppointments().stream().filter(sameDateFilter.and(statusFilter)).map(Appointment::getTime).max(LocalTime::compareTo);
                if (optionalDoctorBookedTill.isEmpty()) {
                    appointmentDoctorDto = new AppointmentDoctorDto(d.getDoctorId(), d.getFirstName() + " " + d.getLastName(), d.getExperience(), userAppointment.getSpeciality(), (d.getEntryTime()), userAppointment.getDate());
                    availableDoctors.add(appointmentDoctorDto);
                }
                if (optionalDoctorBookedTill.isPresent()) {
                    doctorBookedTill = optionalDoctorBookedTill.get();
                    if ((doctorBookedTill.equals(currentTime) || doctorBookedTill.isBefore(currentTime))
                    && currentTime.plusMinutes(60).getHour() + 1 < d.getExitTime().getHour())
                    {
                        appointmentDoctorDto = new AppointmentDoctorDto(d.getDoctorId(), d.getFirstName() + " " + d.getLastName(), d.getExperience(), userAppointment.getSpeciality(), currentTime.plusHours(1), userAppointment.getDate());
                        availableDoctors.add(appointmentDoctorDto);
                    }
                    else if(doctorBookedTill.isAfter(currentTime)
                            && doctorBookedTill.plusMinutes(60).getHour() + 1 < d.getExitTime().getHour()){
                        appointmentDoctorDto = new AppointmentDoctorDto(d.getDoctorId(), d.getFirstName() + " " + d.getLastName(), d.getExperience(), userAppointment.getSpeciality(), doctorBookedTill.plusHours(1), userAppointment.getDate());
                        availableDoctors.add(appointmentDoctorDto);
                    }
                }
            }
        }
        if (availableDoctors.size() == 0) {
            throw new NoDoctorAvailableExcepetion();
        }
        availableDoctors.sort(Comparator.comparingInt(AppointmentDoctorDto::retrieveBookingTimeInHour));
        globalAvailableDoctors.addAll(availableDoctors);
        return availableDoctors.get(0);
    }

    public AppointmentDoctorDto bookAppointment(Appointment appointment, int userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty())
            throw new UserNotFoundException();

        String title = appointment.getSpeciality().toLowerCase();
        if (!specialityRepository.existsByTitle(title)) {
            throw new SpecialityException();
        }
        List<DoctorInter> doctorInterList = doctorRepository.findDoctorsWithSpeciality(specialityRepository.findByTitle(title).getSpecialityId());
        if (doctorInterList.size() == 0) {
            throw new NoSpecialistFoundExcpetion();
        }
        List<Doctor> doctors = doctorInterList.stream().map(DoctorInter::getDoctorId).map(doctorRepository::findById).filter(Optional::isPresent).map(Optional::get).toList();
        AppointmentDoctorDto appointmentDoctorDto = checkSchedule(new ArrayList<>(doctors), appointment);
        Optional<Doctor> d = doctorRepository.findById(appointmentDoctorDto.getDoctorId());

        appointment.setDate(appointmentDoctorDto.getBookedDate());
        appointment.setTime(appointmentDoctorDto.getBookingTime());
        appointment.setStatus(AppointmentStatus.BOOKED);
        appointmentDoctorDto.setStatus(AppointmentStatus.BOOKED.label);

        User user = optionalUser.get();
        user.setAppointment(appointment);
        if (d.isPresent()) {
            d.get().setAppointments(appointment);
            Schedule s = new Schedule(appointment.getTime(), appointment.getDate(), d.get(), user, appointment);
            Schedule schedule=  scheduleRepository.save(s);
            appointmentDoctorDto.setAppointmentId(schedule.getAppointment().getAppointmentId());
            return appointmentDoctorDto;
        } else {
            throw new NoDoctorFoundException();
        }
    }

    public ArrayList<AppointmentDoctorDto> bookAppointmentAgain(Appointment appointment, int userId){
        int doctorId = appointmentRepository.findDoctorByAppointmentId(appointment.getAppointmentId());
        AppointmentDoctorDto currentlyAssignedDoctor = globalAvailableDoctors.stream().filter(appointmentDoctorDto -> appointmentDoctorDto.getDoctorId() == doctorId).toList().get(0);
        globalAvailableDoctors.forEach(appointmentDoctorDto -> appointmentDoctorDto.setAppointmentId(appointment.getAppointmentId()));
        globalAvailableDoctors.remove(currentlyAssignedDoctor);
        return globalAvailableDoctors;
    }

    public LocalTime getCurrentLocalTime(){
        LocalTime currentTime = LocalTime.now();
        int minutes = currentTime.getMinute();
        if (minutes >= 30) {
            currentTime = currentTime.plusHours(1);
            currentTime = currentTime.truncatedTo(ChronoUnit.HOURS);
        } else {
            currentTime = currentTime.plusMinutes(30 - minutes).truncatedTo(ChronoUnit.MINUTES);
        }
        return currentTime;
    }
}
