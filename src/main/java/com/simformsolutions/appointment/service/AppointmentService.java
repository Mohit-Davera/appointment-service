package com.simformsolutions.appointment.service;

import com.simformsolutions.appointment.converter.AppointmentDoctorDtoConverter;
import com.simformsolutions.appointment.dto.AppointmentDoctor;
import com.simformsolutions.appointment.dto.appointment.AppointmentDetails;
import com.simformsolutions.appointment.enums.AppointmentStatus;
import com.simformsolutions.appointment.excepetion.*;
import com.simformsolutions.appointment.model.Appointment;
import com.simformsolutions.appointment.model.Doctor;
import com.simformsolutions.appointment.model.Schedule;
import com.simformsolutions.appointment.model.User;
import com.simformsolutions.appointment.projection.DoctorView;
import com.simformsolutions.appointment.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Service
public class AppointmentService {

    private final ScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;

    private final UserRepository userRepository;

    private final AppointmentDoctorDtoConverter appointmentDoctorDtoConverter;
    private final SpecialityRepository specialityRepository;
    private final AppointmentRepository appointmentRepository;

    private final ModelMapper modelMapper;

    public AppointmentService(ScheduleRepository scheduleRepository, DoctorRepository doctorRepository, UserRepository userRepository, AppointmentDoctorDtoConverter appointmentDoctorDtoConverter, SpecialityRepository specialityRepository, AppointmentRepository appointmentRepository, ModelMapper modelMapper) {
        this.scheduleRepository = scheduleRepository;
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.appointmentDoctorDtoConverter = appointmentDoctorDtoConverter;
        this.specialityRepository = specialityRepository;
        this.appointmentRepository = appointmentRepository;
        this.modelMapper = modelMapper;
    }

    private List<AppointmentDoctor> checkSchedule(List<Doctor> doctors, Appointment userAppointment) {
        LocalTime currentTime = getCurrentLocalTime();
        Predicate<Doctor> appointmentNullFilter = doctor -> doctor.getAppointments().isEmpty();
        ArrayList<Doctor> freeDoctors = new ArrayList<>(doctors.stream().filter(appointmentNullFilter).toList());
        List<AppointmentDoctor> availableDoctors = new ArrayList<>(appointmentDoctorDtoConverter.freeDoctorToBookedDoctorConverter(freeDoctors, userAppointment, currentTime));
        if (!freeDoctors.isEmpty()) doctors.removeAll(freeDoctors);
        if (!doctors.isEmpty()) {
            availableDoctors.addAll(checkTimingsOfDoctors(userAppointment, doctors));
        }
        if (availableDoctors.isEmpty())
            throw new DoctorNotAvailableException("No Doctor Available For This Date" + userAppointment.getDate());
        availableDoctors.sort(Comparator.comparingInt(AppointmentDoctor::retrieveBookingTimeInHour));
        return availableDoctors;
    }

    public AppointmentDoctor saveAppointment(AppointmentDetails appointmentDetails, int userId) {
        Appointment appointment = modelMapper.map(appointmentDetails, Appointment.class);
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) throw new UserNotFoundException("User Not Found With This Id" + userId);
        String title = appointment.getSpeciality().toLowerCase();
        if (!specialityRepository.existsByTitle(title))
            throw new SpecialityException("Cannot Find This Speciality" + title);
        List<DoctorView> doctorViewList = doctorRepository.findDoctorsIdWithSpeciality(specialityRepository.findByTitle(title).getSpecialityId());
        if (doctorViewList.isEmpty())
            throw new SpecialistNotFoundException("Cannot Find Specialist For This Speciality " + title);
        List<Doctor> doctors = doctorViewList.stream().map(DoctorView::getDoctorId).map(doctorRepository::findById).filter(Optional::isPresent).map(Optional::get).toList();
        AppointmentDoctor appointmentDoctor = checkSchedule(new ArrayList<>(doctors), appointment).get(0);
        Optional<Doctor> d = doctorRepository.findById(appointmentDoctor.getDoctorId());
        appointment.setDate(appointmentDoctor.getBookedDate());
        appointment.setEndTime(appointmentDoctor.getBookingTime().plusHours(1));
        appointment.setStatus(AppointmentStatus.BOOKED);
        appointmentDoctor.setStatus(AppointmentStatus.BOOKED.label);
        User user = optionalUser.get();
        user.addAppointment(appointment);
        if (d.isPresent()) {
            d.get().addAppointment(appointment);
            Schedule s = new Schedule(appointment.getEndTime(), appointment.getDate(), d.get(), user, appointment);
            Schedule schedule = scheduleRepository.save(s);
            appointmentDoctor.setAppointmentId(schedule.getAppointment().getAppointmentId());
            return appointmentDoctor;
        } else throw new DoctorNotFoundException("Cannot Find Doctor");
    }

    public List<AppointmentDoctor> bookAppointmentAgain(Appointment appointment, int userId) {
        if (!userRepository.existsById(userId)) throw new UserNotFoundException("User Not Found With This Id" + userId);
        List<Doctor> doctors = doctorRepository.findDoctorsWithSpeciality(specialityRepository.findByTitle(appointment.getSpeciality()).getSpecialityId());
        List<AppointmentDoctor> availableDoctors = checkSchedule(new ArrayList<>(doctors), appointment);
        int doctorId = appointmentRepository.findDoctorByAppointmentId(appointment.getAppointmentId());
        availableDoctors.forEach(appointmentDoctorDto -> appointmentDoctorDto.setAppointmentId(appointment.getAppointmentId()));
        return availableDoctors.stream().filter(appointmentDoctorDto -> appointmentDoctorDto.getDoctorId() != doctorId).toList();
    }

    private LocalTime getCurrentLocalTime() {
        LocalTime currentTime = LocalTime.now();
        int minutes = currentTime.getMinute();
        if (minutes >= 30) {
            currentTime = currentTime.plusHours(1);
            currentTime = currentTime.truncatedTo(ChronoUnit.HOURS);
        } else {
            currentTime = currentTime.plusMinutes(30L - minutes).truncatedTo(ChronoUnit.MINUTES);
        }
        return currentTime;
    }

    private List<AppointmentDoctor> checkTimingsOfDoctors(Appointment userAppointment, List<Doctor> doctors) {
        AppointmentDoctor appointmentDoctor;
        LocalTime doctorBookedTillTime;
        LocalDateTime doctorBookedTillDateTime;
        List<AppointmentDoctor> doctorFreeAfterAppointments = new ArrayList<>();
        LocalTime currentTime = getCurrentLocalTime();
        LocalDateTime currentDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Predicate<Appointment> sameDateFilter = appointment -> appointment.getDate().equals(userAppointment.getDate());
        Predicate<Appointment> statusFilter = appointment -> appointment.getStatus().equals(AppointmentStatus.BOOKED);
        for (Doctor d : doctors) {
            Optional<LocalDateTime> optionalDoctorBookedTillDateTime = d.getAppointments().stream().filter(sameDateFilter.and(statusFilter)).map(
                    appointment -> LocalDateTime.of(appointment.getDate(), appointment.getEndTime())
            ).max(LocalDateTime::compareTo);
            if (optionalDoctorBookedTillDateTime.isEmpty()) {
                doctorBookedTillTime = userAppointment.getDate().getDayOfMonth() <= LocalDate.now().getDayOfMonth() ? currentTime : d.getEntryTime();
                appointmentDoctor = new AppointmentDoctor(d.getDoctorId(), d.getFirstName() + " " + d.getLastName(), d.getExperience(), userAppointment.getSpeciality(), (doctorBookedTillTime), userAppointment.getDate());
                doctorFreeAfterAppointments.add(appointmentDoctor);
            }
            if (optionalDoctorBookedTillDateTime.isPresent()) {
                doctorBookedTillDateTime = optionalDoctorBookedTillDateTime.get();
                if ((doctorBookedTillDateTime.equals(currentDateTime) || doctorBookedTillDateTime.isBefore(currentDateTime)) && currentDateTime.plusMinutes(60).getHour() + 1 < d.getExitTime().getHour()) {
                    appointmentDoctor = new AppointmentDoctor(d.getDoctorId(), d.getFirstName() + " " + d.getLastName(), d.getExperience(), userAppointment.getSpeciality(), currentTime, userAppointment.getDate());
                    doctorFreeAfterAppointments.add(appointmentDoctor);
                } else if (doctorBookedTillDateTime.isAfter(currentDateTime) && doctorBookedTillDateTime.plusMinutes(60).getHour() + 1 < d.getExitTime().getHour()) {
                    String pattern = "HH:m";
                    doctorBookedTillTime = LocalTime.parse(doctorBookedTillDateTime.getHour() + ":" + doctorBookedTillDateTime.getMinute(), DateTimeFormatter.ofPattern(pattern));
                    appointmentDoctor = new AppointmentDoctor(d.getDoctorId(), d.getFirstName() + " " + d.getLastName(), d.getExperience(), userAppointment.getSpeciality(), doctorBookedTillTime, userAppointment.getDate());
                    doctorFreeAfterAppointments.add(appointmentDoctor);
                }
            }
        }
        return doctorFreeAfterAppointments;
    }
}
