package com.simformsolutions.appointment.service;

import com.simformsolutions.appointment.converter.AppointmentDoctorDtoConverter;
import com.simformsolutions.appointment.dto.AppointmentDoctorDto;
import com.simformsolutions.appointment.dto.appointment.AppointmentDetailsDto;
import com.simformsolutions.appointment.dto.user.UserDetailsDto;
import com.simformsolutions.appointment.enums.AppointmentStatus;
import com.simformsolutions.appointment.excepetion.NoAppointmentFoundException;
import com.simformsolutions.appointment.excepetion.ScheduleNotFoundException;
import com.simformsolutions.appointment.excepetion.StatusChangeException;
import com.simformsolutions.appointment.model.Appointment;
import com.simformsolutions.appointment.model.Doctor;
import com.simformsolutions.appointment.model.Schedule;
import com.simformsolutions.appointment.model.User;
import com.simformsolutions.appointment.repository.AppointmentRepository;
import com.simformsolutions.appointment.repository.DoctorRepository;
import com.simformsolutions.appointment.repository.ScheduleRepository;
import com.simformsolutions.appointment.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final DoctorRepository doctorRepository;

    private final AppointmentService appointmentService;

    private final AppointmentDoctorDtoConverter appointmentDoctorDtoConverter;

    private final AppointmentRepository appointmentRepository;

    private final ScheduleRepository scheduleRepository;

    private final ModelMapper modelMapper;

    public UserService(UserRepository userRepository, AppointmentService appointmentService, AppointmentDoctorDtoConverter appointmentDoctorDtoConverter, AppointmentRepository appointmentRepository, ScheduleRepository scheduleRepository, DoctorRepository doctorRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.appointmentService = appointmentService;
        this.appointmentDoctorDtoConverter = appointmentDoctorDtoConverter;
        this.appointmentRepository = appointmentRepository;
        this.scheduleRepository = scheduleRepository;
        this.doctorRepository = doctorRepository;
        this.modelMapper = modelMapper;
    }

    public List<AppointmentDoctorDto> getAppointments(int userId) {
        return appointmentDoctorDtoConverter.tuplesToAppointmentDoctorConverter(appointmentRepository.findDetailsOfAppointments(userId));
    }

    public UserDetailsDto addUser(UserDetailsDto userDetailsDto) {
        try {
            userDetailsDto.setUserId(userRepository.save(modelMapper.map(userDetailsDto, User.class)).getUserId());
        } catch (Exception ex) {
            throw new DataIntegrityViolationException("Email Already Exists");
        }

        return userDetailsDto;
    }

    public AppointmentDoctorDto rescheduleAppointment(int appointmentId, int userId, String days) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentId);
        int rescheduleDays = Integer.parseInt(days);
        if (optionalAppointment.isPresent()) {
            Appointment appointment = optionalAppointment.get();
            if (appointment.getEndTime().isBefore(LocalTime.now())) {
                throw new StatusChangeException();
            }
            if (appointment.getStatus().equals(AppointmentStatus.CANCELLED)) {
                throw new StatusChangeException();
            }
            if (rescheduleDays > 0) {
                appointment.setDate(appointment.getDate().plusDays(rescheduleDays));
                appointmentService.saveAppointment(modelMapper.map(appointment, AppointmentDetailsDto.class), userId);
            }
            appointment.setStatus(AppointmentStatus.RESCHEDULE);
            appointmentRepository.save(appointment);
            return appointmentDoctorDtoConverter.tuplesToAppointmentDoctorConverter(appointmentRepository.findDetailsOfAppointment(appointmentId)).get(0);
        } else {
            throw new NoAppointmentFoundException();
        }
    }

    public AppointmentDoctorDto cancelAppointment(int appointmentId) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentId);
        if (optionalAppointment.isPresent()) {
            Appointment appointment = optionalAppointment.get();
            appointment.setStatus(AppointmentStatus.CANCELLED);
            appointmentRepository.save(appointment);
            return appointmentDoctorDtoConverter.tuplesToAppointmentDoctorConverter(appointmentRepository.findDetailsOfAppointment(appointmentId)).get(0);
        } else {
            throw new NoAppointmentFoundException();
        }
    }

    public List<AppointmentDoctorDto> getAvailableDoctors(int appointmentId, int userId) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentId);
        if (optionalAppointment.isPresent()) {
            Appointment appointment = optionalAppointment.get();
            return appointmentService.bookAppointmentAgain(appointment, userId);
        } else {
            throw new NoAppointmentFoundException();
        }
    }

    public AppointmentDoctorDto changeDoctor(AppointmentDoctorDto appointmentDoctorDto, int userId) {
        Optional<Schedule> optionalSchedule = scheduleRepository.getScheduleFromAppointmentId(appointmentDoctorDto.getAppointmentId());
        Optional<User> optionalUser = userRepository.findById(userId);
        Optional<Doctor> optionalDoctor = doctorRepository.findById(appointmentDoctorDto.getDoctorId());
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentDoctorDto.getAppointmentId());
        if (optionalSchedule.isPresent() && optionalUser.isPresent() && optionalDoctor.isPresent() && optionalAppointment.isPresent()) {

            Schedule schedule = optionalSchedule.get();
            User User = optionalUser.get();
            Appointment appointment = optionalAppointment.get();
            Doctor newBookedDoctor = optionalDoctor.get();

            appointment.setEndTime(appointmentDoctorDto.getBookingTime());
            appointment.setDate(appointmentDoctorDto.getBookedDate());
            appointment.setStatus(AppointmentStatus.BOOKED);
            appointmentDoctorDto.setStatus(AppointmentStatus.BOOKED.label);

            User.setAppointment(appointment);
            newBookedDoctor.addAppointment(appointment);
            schedule.setAppointment(appointment);
            appointmentDoctorDto.setAppointmentId(scheduleRepository.save(schedule).getAppointment().getAppointmentId());
            return appointmentDoctorDto;
        } else {
            throw new ScheduleNotFoundException();
        }

    }
}
