package com.simformsolutions.doctorappointmentsystem.service;

import com.simformsolutions.doctorappointmentsystem.converter.AppointmentDoctorDtoConverter;
import com.simformsolutions.doctorappointmentsystem.dto.AppointmentDoctorDto;
import com.simformsolutions.doctorappointmentsystem.enums.AppointmentStatus;
import com.simformsolutions.doctorappointmentsystem.excepetionhandler.NoAppointmentFoundException;
import com.simformsolutions.doctorappointmentsystem.excepetionhandler.ScheduleNotFoundException;
import com.simformsolutions.doctorappointmentsystem.excepetionhandler.StatusChangeException;
import com.simformsolutions.doctorappointmentsystem.model.Appointment;
import com.simformsolutions.doctorappointmentsystem.model.Doctor;
import com.simformsolutions.doctorappointmentsystem.model.Schedule;
import com.simformsolutions.doctorappointmentsystem.model.User;
import com.simformsolutions.doctorappointmentsystem.repository.AppointmentRepository;
import com.simformsolutions.doctorappointmentsystem.repository.DoctorRepository;
import com.simformsolutions.doctorappointmentsystem.repository.ScheduleRepository;
import com.simformsolutions.doctorappointmentsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final DoctorRepository doctorRepository;
    @Autowired
    private final AppointmentService appointmentService;
    @Autowired
    private final AppointmentDoctorDtoConverter appointmentDoctorDtoConverter;
    @Autowired
    private final AppointmentRepository appointmentRepository;
    @Autowired
    private final ScheduleRepository scheduleRepository;

    public UserService(UserRepository userRepository, AppointmentService appointmentService, AppointmentDoctorDtoConverter appointmentDoctorDtoConverter, AppointmentRepository appointmentRepository,ScheduleRepository scheduleRepository,DoctorRepository doctorRepository) {
        this.userRepository = userRepository;
        this.appointmentService = appointmentService;
        this.appointmentDoctorDtoConverter = appointmentDoctorDtoConverter;
        this.appointmentRepository = appointmentRepository;
        this.scheduleRepository = scheduleRepository;
        this.doctorRepository = doctorRepository;
    }

    public List<AppointmentDoctorDto> getAppointments(int userId){
        return appointmentDoctorDtoConverter.tuplesToAppointmentDoctorConverter(appointmentRepository.findDetailsOfAppointments(userId));
    }

    public User addUser(User user){
        return userRepository.save(user);
    }

    public AppointmentDoctorDto rescheduleAppointments(int appointmentId,int userId,String days) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentId);
        int rescheduleDays = Integer.parseInt(days);
        if (optionalAppointment.isPresent()){
            Appointment appointment= optionalAppointment.get();
            if(appointment.getTime().isBefore(LocalTime.now())){
                throw new StatusChangeException();
            }
            if(appointment.getStatus().equals(AppointmentStatus.CANCELLED)){
                throw new StatusChangeException();
            }
            if (rescheduleDays > 0){
                appointment.setDate(appointment.getDate().plusDays(rescheduleDays));
                appointmentService.bookAppointment(appointment,userId);
            }
            appointment.setStatus(AppointmentStatus.RESCHEDULE);
            appointmentRepository.save(appointment);
            return appointmentDoctorDtoConverter.tuplesToAppointmentDoctorConverter(appointmentRepository.findDetailsOfAppointment(appointmentId)).get(0);
        }
        else {
            throw new NoAppointmentFoundException();
        }
    }

    public AppointmentDoctorDto cancelAppointment(int appointmentId) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentId);
        if (optionalAppointment.isPresent()){
            Appointment appointment= optionalAppointment.get();
            appointment.setStatus(AppointmentStatus.CANCELLED);
            appointmentRepository.save(appointment);
            return appointmentDoctorDtoConverter.tuplesToAppointmentDoctorConverter(appointmentRepository.findDetailsOfAppointment(appointmentId)).get(0);
        }
        else {
            throw new NoAppointmentFoundException();
        }
    }

    public ArrayList<AppointmentDoctorDto> availableDoctors(int appointmentId, int userId) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentId);
        if(optionalAppointment.isPresent()){
            Appointment appointment = optionalAppointment.get();
            return appointmentService.bookAppointmentAgain(appointment,userId);
        }
        else {
            throw new NoAppointmentFoundException();
        }
    }

    public AppointmentDoctorDto changeDoctor(AppointmentDoctorDto appointmentDoctorDto, int userId) {
        Optional<Schedule> optionalSchedule=scheduleRepository.getScheduleFromAppointmentId(appointmentDoctorDto.getAppointmentId());
        Optional<User> optionalUser = userRepository.findById(userId);
        Optional<Doctor> optionalDoctor = doctorRepository.findById(appointmentDoctorDto.getDoctorId());
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentDoctorDto.getAppointmentId());
        if(optionalSchedule.isPresent() && optionalUser.isPresent() && optionalDoctor.isPresent() && optionalAppointment.isPresent()){

            Schedule schedule = optionalSchedule.get();
            User User = optionalUser.get();
            Appointment appointment = optionalAppointment.get();
            Doctor newBookedDoctor = optionalDoctor.get();

            appointment.setTime(appointmentDoctorDto.getBookingTime());
            appointment.setDate(appointmentDoctorDto.getBookedDate());
            appointment.setStatus(AppointmentStatus.BOOKED);
            appointmentDoctorDto.setStatus(AppointmentStatus.BOOKED.label);

            User.setAppointment(appointment);
            newBookedDoctor.setAppointments(appointment);
            schedule.setAppointment(appointment);
            appointmentDoctorDto.setAppointmentId(scheduleRepository.save(schedule).getAppointment().getAppointmentId());
            return appointmentDoctorDto;
        }
        else {
            throw new ScheduleNotFoundException();
        }

    }
}
