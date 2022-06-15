package com.simformsolutions.doctorappointmentsystem.service;

import com.simformsolutions.doctorappointmentsystem.NoDoctorAvailableExcepetion;
import com.simformsolutions.doctorappointmentsystem.dto.AppointmentDoctorDto;
import com.simformsolutions.doctorappointmentsystem.enums.AppointmentStatus;
import com.simformsolutions.doctorappointmentsystem.model.Appointment;
import com.simformsolutions.doctorappointmentsystem.model.Doctor;
import com.simformsolutions.doctorappointmentsystem.model.Schedule;
import com.simformsolutions.doctorappointmentsystem.model.User;
import com.simformsolutions.doctorappointmentsystem.projection.DoctorInter;
import com.simformsolutions.doctorappointmentsystem.repository.DoctorRepository;
import com.simformsolutions.doctorappointmentsystem.repository.ScheduleRepository;
import com.simformsolutions.doctorappointmentsystem.repository.SpecialityRepository;
import com.simformsolutions.doctorappointmentsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
    private ScheduleRepository scheduleRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SpecialityRepository specialityRepository;

    public AppointmentDoctorDto checkSchedule(ArrayList<Doctor> doctors, Appointment userAppointment) {

        Predicate<Appointment> sameDateFilter = appointment -> appointment.getDate().equals(userAppointment.getDate());
        Predicate<Appointment> statusFilter = appointment -> appointment.getStatus().equals(AppointmentStatus.BOOKED);
        Predicate<Doctor> appointmentNullFilter = doctor -> doctor.getAppointments().size() == 0;

        ArrayList<Doctor> freeDoctors = new ArrayList<>(doctors.stream().filter(
                appointmentNullFilter).toList());
        List<AppointmentDoctorDto> availableDoctors = new ArrayList<>(freeDoctorToBookedDoctorConverter(freeDoctors, userAppointment));
        if (freeDoctors.size() != 0) {
            doctors.removeAll(freeDoctors);
        }
        if(doctors.size() !=0){
            AppointmentDoctorDto appointmentDoctorDto;
            for (Doctor d : doctors) {
                Optional<LocalTime> doctorBookedTill = d.getAppointments().stream().filter(sameDateFilter.and(statusFilter)).map(Appointment::getTime).max(LocalTime::compareTo);
                if(doctorBookedTill.isEmpty() && !isAllBooked(d)){
                    appointmentDoctorDto = new AppointmentDoctorDto(d.getDoctorId(), d.getFirstName()+ " "+ d.getLastName(), d.getExperience(),userAppointment.getSpeciality(), (d.getEntryTime().plusHours(1L)), userAppointment.getDate());
                    availableDoctors.add(appointmentDoctorDto);
                }
                else if(doctorBookedTill.isPresent() && doctorBookedTill.get().isBefore(d.getExitTime().minusHours(1L))) {
                    appointmentDoctorDto = new AppointmentDoctorDto(d.getDoctorId(),d.getFirstName()+" "+d.getLastName(), d.getExperience(), userAppointment.getSpeciality(), (doctorBookedTill.get().plusHours(1L)), userAppointment.getDate());
                    availableDoctors.add(appointmentDoctorDto);
                }

            }
        }
        if(availableDoctors.size() == 0){
            throw new NoDoctorAvailableExcepetion();
        }
        availableDoctors.sort(Comparator.comparingInt(AppointmentDoctorDto::retriveBookingTimeInHour));
        return availableDoctors.get(0);
    }

    public Object bookAppointment(Appointment appointment, int userId) {
        String title = appointment.getSpeciality().toLowerCase();
        if (!specialityRepository.existsByTitle(title)) {
            return "Enter Appropriate Specialty";
        }
        List<DoctorInter> doctorInterList = doctorRepository.findDoctorsWithSpeciality(specialityRepository.findByTitle(title).getSpecialityId());
        if (doctorInterList.size() == 0) {
            return "No Doctors Are There";
        }
        List<Doctor> doctors = doctorInterList.stream().map(DoctorInter::getDoctorId).map(doctorRepository::findById).filter(Optional::isPresent).map(Optional::get).toList();
        AppointmentDoctorDto appointmentDoctorDto = checkSchedule(new ArrayList<>(doctors), appointment);
        Optional<Doctor> d = doctorRepository.findById(appointmentDoctorDto.getDoctorId());
        Optional<User> opUser = userRepository.findById(userId);

        //Will LOGIC to find free slot

        appointment.setDate(appointmentDoctorDto.getBookedDate());
        appointment.setTime(appointmentDoctorDto.getBookingTime());
        appointment.setStatus(AppointmentStatus.BOOKED);
        if (opUser.isPresent()) {
            User user = opUser.get();
            user.setAppointment(appointment);
            if (d.isPresent()) {
                d.get().setAppointments(appointment);
                Schedule s = new Schedule(appointment.getTime(), appointment.getDate(), d.get(), user, appointment);
                scheduleRepository.save(s);
                return appointmentDoctorDto;
            }
        }
        return "User Not Found";
    }

    public List<AppointmentDoctorDto> freeDoctorToBookedDoctorConverter(List<Doctor> doctors, Appointment userAppointment)
    {
            List<AppointmentDoctorDto> bookedDoctors = new ArrayList<>();
        doctors.forEach(
                d -> bookedDoctors.add(
                        new AppointmentDoctorDto(d.getDoctorId(),  d.getFirstName()+" "+d.getLastName(),
                                d.getExperience(),
                                userAppointment.getSpeciality(),
                                d.getEntryTime(),
                                userAppointment.getDate()
                                )
                ));
        return bookedDoctors;
    }

    public boolean isAllBooked(Doctor d){
        int bookedAppointments = d.getAppointments().stream().filter(appointment -> appointment.getStatus().equals(AppointmentStatus.BOOKED)).toList().size();
        int slots = (int)d.getEntryTime().until(d.getExitTime(), ChronoUnit.HOURS);
        return slots<bookedAppointments;

    }
}
