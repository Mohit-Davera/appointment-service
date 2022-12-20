package com.simformsolutions.appointment.service;

import com.simformsolutions.appointment.converter.AppointmentDoctorDtoConverter;
import com.simformsolutions.appointment.dto.AppointmentDoctor;
import com.simformsolutions.appointment.dto.appointment.AppointmentDetails;
import com.simformsolutions.appointment.dto.user.UserInformation;
import com.simformsolutions.appointment.enums.AppointmentStatus;
import com.simformsolutions.appointment.enums.Provider;
import com.simformsolutions.appointment.excepetion.AppointmentNotFoundException;
import com.simformsolutions.appointment.excepetion.ScheduleNotFoundException;
import com.simformsolutions.appointment.excepetion.StatusChangeException;
import com.simformsolutions.appointment.model.*;
import com.simformsolutions.appointment.repository.*;
import com.simformsolutions.appointment.service.oauth.CryptoPrincipal;
import com.simformsolutions.appointment.service.oauth.CustomOAuth2User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    static final String APPOINTMENT_MESSAGE = "Cannot Find Appointment With This Id ";
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentService appointmentService;
    private final AppointmentDoctorDtoConverter appointmentDoctorDtoConverter;
    private final AppointmentRepository appointmentRepository;
    private final ScheduleRepository scheduleRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;


    public UserService(UserRepository userRepository, DoctorRepository doctorRepository, AppointmentService appointmentService, AppointmentDoctorDtoConverter appointmentDoctorDtoConverter, AppointmentRepository appointmentRepository, ScheduleRepository scheduleRepository, ModelMapper modelMapper,RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentService = appointmentService;
        this.appointmentDoctorDtoConverter = appointmentDoctorDtoConverter;
        this.appointmentRepository = appointmentRepository;
        this.scheduleRepository = scheduleRepository;
        this.modelMapper = modelMapper;
        this.roleRepository = roleRepository;
    }

    public List<AppointmentDoctor> getAppointments(int userId) {
        return appointmentDoctorDtoConverter.tuplesToAppointmentDoctorConverter(appointmentRepository.findDetailsOfAppointments(userId));
    }

    public UserInformation addUser(UserInformation userInformation) {
        try {
            Optional<Role> optionalRole = roleRepository.findById(1);
            userInformation.setPassword(bCryptPasswordEncoder.encode(userInformation.getPassword()));
            userInformation.setEnabled(true);
            if (optionalRole.isPresent()) {
                optionalRole.get().addRoleDetail(new RoleDetails(userInformation.getEmail()));
                roleRepository.save(optionalRole.get());
            }
            userInformation.setUserId(userRepository.save(modelMapper.map(userInformation, User.class)).getUserId());
        } catch (DataIntegrityViolationException exception) {
            throw new DataIntegrityViolationException("This Email Already Exists " + userInformation.getEmail());
        }
        return userInformation;
    }

    public AppointmentDoctor rescheduleAppointment(int appointmentId, int userId, String days) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentId);
        int rescheduleDays = Integer.parseInt(days);
        if (optionalAppointment.isPresent()) {
            Appointment appointment = optionalAppointment.get();
            if (LocalDateTime.of(appointment.getDate(), appointment.getEndTime()).isBefore(LocalDateTime.now())) {
                throw new StatusChangeException("Cannot Change Status Of This Appointment As It Is In The Past");
            }
            if (appointment.getStatus().equals(AppointmentStatus.CANCELLED)) {
                throw new StatusChangeException("Cannot Change Status Of This Appointment As It's Cancelled");
            }
            if (rescheduleDays > 0) {
                appointment.setDate(appointment.getDate().plusDays(rescheduleDays));
                appointmentService.saveAppointment(modelMapper.map(appointment, AppointmentDetails.class), userId);
            }
            appointment.setStatus(AppointmentStatus.RESCHEDULE);
            appointmentRepository.save(appointment);
            return appointmentDoctorDtoConverter.tuplesToAppointmentDoctorConverter(appointmentRepository.findDetailsOfAppointment(appointmentId)).get(0);
        } else {
            throw new AppointmentNotFoundException(APPOINTMENT_MESSAGE + appointmentId);
        }
    }

    public AppointmentDoctor cancelAppointment(int appointmentId) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentId);
        if (optionalAppointment.isPresent()) {
            Appointment appointment = optionalAppointment.get();
            appointment.setStatus(AppointmentStatus.CANCELLED);
            appointmentRepository.save(appointment);
            return appointmentDoctorDtoConverter.tuplesToAppointmentDoctorConverter(appointmentRepository.findDetailsOfAppointment(appointmentId)).get(0);
        } else {
            throw new AppointmentNotFoundException(APPOINTMENT_MESSAGE + appointmentId);
        }
    }

    public List<AppointmentDoctor> getAvailableDoctors(int appointmentId, int userId) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentId);
        if (optionalAppointment.isPresent()) {
            Appointment appointment = optionalAppointment.get();
            return appointmentService.bookAppointmentAgain(appointment, userId);
        } else {
            throw new AppointmentNotFoundException(APPOINTMENT_MESSAGE + appointmentId);
        }
    }

    public AppointmentDoctor changeDoctor(AppointmentDoctor appointmentDoctor, int userId) {
        Optional<Schedule> optionalSchedule = scheduleRepository.getScheduleFromAppointmentId(appointmentDoctor.getAppointmentId());
        Optional<User> optionalUser = userRepository.findById(userId);
        Optional<Doctor> optionalDoctor = doctorRepository.findById(appointmentDoctor.getDoctorId());
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentDoctor.getAppointmentId());
        if (optionalSchedule.isPresent() && optionalUser.isPresent() && optionalDoctor.isPresent() && optionalAppointment.isPresent()) {
            Schedule schedule = optionalSchedule.get();
            User user = optionalUser.get();
            Appointment appointment = optionalAppointment.get();
            Doctor newBookedDoctor = optionalDoctor.get();
            appointment.setEndTime(appointmentDoctor.getBookingTime());
            appointment.setDate(appointmentDoctor.getBookedDate());
            appointment.setStatus(AppointmentStatus.BOOKED);
            appointmentDoctor.setStatus(AppointmentStatus.BOOKED.label);
            user.addAppointment(appointment);
            newBookedDoctor.addAppointment(appointment);
            schedule.setAppointment(appointment);
            appointmentDoctor.setAppointmentId(scheduleRepository.save(schedule).getAppointment().getAppointmentId());
            return appointmentDoctor;
        } else {
            throw new ScheduleNotFoundException("Cannot Find Schedule With This Appointment Id" + appointmentDoctor.getDoctorId());
        }

    }

    public void processOAuthPostLogin(CustomOAuth2User customOAuth2User) {
        Optional<User> existUser = userRepository.findByEmail(customOAuth2User.getEmail());
        if (existUser.isEmpty()) {
            User newUser = new User();
            newUser.setName(customOAuth2User.getName());
            newUser.setEmail(customOAuth2User.getEmail());
            newUser.setProvider(Provider.GOOGLE);
            newUser.setEnabled(true);
            newUser.setPassword("");
            userRepository.save(newUser);
        }

    }
    public void processOAuthPostLogin(CryptoPrincipal cryptoPrincipal) {
        Optional<User> existUser = userRepository.findByEmail(cryptoPrincipal.getEmail());
        if (existUser.isEmpty()) {
            User newUser = new User();
            newUser.setName(cryptoPrincipal.getFullName());
            newUser.setEmail(cryptoPrincipal.getEmail());
            newUser.setProvider(Provider.KEYCLOAK);
            newUser.setEnabled(true);
            newUser.setPassword("");
            userRepository.save(newUser);
        }
    }

    public boolean checkIfExists(String email){
       return userRepository.existsByEmail(email);
    }
}
