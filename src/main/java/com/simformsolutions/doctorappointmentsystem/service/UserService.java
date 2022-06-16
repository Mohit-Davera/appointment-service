package com.simformsolutions.doctorappointmentsystem.service;

import com.simformsolutions.doctorappointmentsystem.converter.AppointmentDoctorDtoConverter;
import com.simformsolutions.doctorappointmentsystem.dto.AppointmentDoctorDto;
import com.simformsolutions.doctorappointmentsystem.enums.AppointmentStatus;
import com.simformsolutions.doctorappointmentsystem.excepetionhandler.NoAppointmentFoundException;
import com.simformsolutions.doctorappointmentsystem.excepetionhandler.StatusChangeException;
import com.simformsolutions.doctorappointmentsystem.model.Appointment;
import com.simformsolutions.doctorappointmentsystem.model.Doctor;
import com.simformsolutions.doctorappointmentsystem.model.User;
import com.simformsolutions.doctorappointmentsystem.repository.AppointmentRepository;
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
    private final AppointmentService appointmentService;
    @Autowired
    private final AppointmentDoctorDtoConverter appointmentDoctorDtoConverter;
    @Autowired
    private final AppointmentRepository appointmentRepository;

    public UserService(UserRepository userRepository, AppointmentService appointmentService, AppointmentDoctorDtoConverter appointmentDoctorDtoConverter, AppointmentRepository appointmentRepository) {
        this.userRepository = userRepository;
        this.appointmentService = appointmentService;
        this.appointmentDoctorDtoConverter = appointmentDoctorDtoConverter;
        this.appointmentRepository = appointmentRepository;
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

    public ArrayList<Doctor> availableDoctors(int appointmentId, int userId) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentId);
        if(optionalAppointment.isPresent()){
            Appointment appointment = optionalAppointment.get();
            return appointmentService.bookAppointment(appointment,userId,true);
        }
        else {
            throw new NoAppointmentFoundException();
        }
    }
}
