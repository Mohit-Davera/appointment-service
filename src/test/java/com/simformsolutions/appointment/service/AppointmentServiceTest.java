package com.simformsolutions.appointment.service;

import com.simformsolutions.appointment.converter.AppointmentDoctorDtoConverter;
import com.simformsolutions.appointment.dto.AppointmentDoctorDto;
import com.simformsolutions.appointment.dto.appointment.AppointmentDetailsDto;
import com.simformsolutions.appointment.enums.AppointmentStatus;
import com.simformsolutions.appointment.excepetion.NoDoctorAvailableExcepetion;
import com.simformsolutions.appointment.excepetion.NoSpecialistFoundExcpetion;
import com.simformsolutions.appointment.excepetion.SpecialityException;
import com.simformsolutions.appointment.excepetion.UserNotFoundException;
import com.simformsolutions.appointment.model.*;
import com.simformsolutions.appointment.projection.DoctorView;
import com.simformsolutions.appointment.repository.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("SameParameterValue")
class AppointmentServiceTest {

    static final AppointmentDoctorDto APPOINTMENT_DOCTOR_DTO1 = new AppointmentDoctorDto(1, 1, "Ravi D", 2, "ayurveda", LocalTime.now(), LocalDate.now(), "BOOKED");
    static final AppointmentDetailsDto APPOINTMENT_DETAILS_DTO = new AppointmentDetailsDto("ayurveda", "random issue", LocalDate.now(), "random user");
    static final AppointmentDoctorDto APPOINTMENT_DOCTOR_DTO2 = new AppointmentDoctorDto(2, 1, "Ravi D", 2, "ayurveda", LocalTime.now(), LocalDate.now(), "BOOKED");
    static final List<AppointmentDoctorDto> APPOINTMENT_DOCTOR_DTOS = Arrays.asList(APPOINTMENT_DOCTOR_DTO1, APPOINTMENT_DOCTOR_DTO2);

    static final String SPECIALITY_TITLE = "ayurveda";

    @MockBean
    private ModelMapper modelMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AppointmentRepository appointmentRepository;
    @MockBean
    private DoctorRepository doctorRepository;

    @MockBean
    private ScheduleRepository scheduleRepository;

    @MockBean
    private SpecialityRepository specialityRepository;

    @MockBean
    private AppointmentDoctorDtoConverter appointmentDoctorDtoConverter;

    @MockBean
    private UserService userService;

    @Autowired
    private AppointmentService appointmentService;

    @Test
    void saveAppointmentAppointmentFailure() {

        //Expected
        Mockito.when(modelMapper.map(any(AppointmentDetailsDto.class), any())).thenReturn(getOptionalAppointment(false, false, false, false).orElse(null));
        Mockito.when(userRepository.findById(1)).thenReturn(getOptionalUser(true));
        assertThrows(UserNotFoundException.class, () -> appointmentService.saveAppointment(APPOINTMENT_DETAILS_DTO, 1));
    }

    @Test
    void saveAppointmentSpecialityFailure() {

        //Expected
        Mockito.when(modelMapper.map(any(AppointmentDetailsDto.class), any())).thenReturn(getOptionalAppointment(false, false, false, false).orElse(null));
        Mockito.when(userRepository.findById(1)).thenReturn(getOptionalUser(false));
        Mockito.when(specialityRepository.existsByTitle(SPECIALITY_TITLE)).thenReturn(false);
        assertThrows(SpecialityException.class, () -> appointmentService.saveAppointment(APPOINTMENT_DETAILS_DTO, 1));
    }

    @Test
    void saveAppointmentSpecialistFailure() {

        //Expected
        Mockito.when(modelMapper.map(any(AppointmentDetailsDto.class), any())).thenReturn(getOptionalAppointment(false, false, false, false).orElse(null));
        Mockito.when(userRepository.findById(1)).thenReturn(getOptionalUser(false));
        Mockito.when(specialityRepository.existsByTitle(SPECIALITY_TITLE)).thenReturn(true);
        Mockito.when(specialityRepository.findByTitle(SPECIALITY_TITLE)).thenReturn(new Speciality(1, SPECIALITY_TITLE, null));
        Mockito.when(doctorRepository.findDoctorsIdWithSpeciality(1)).thenReturn(new ArrayList<>());
        assertThrows(NoSpecialistFoundExcpetion.class, () -> appointmentService.saveAppointment(APPOINTMENT_DETAILS_DTO, 1));
    }

    @Test
    void saveAppointmentDoctorFailure() {

        //Expected
        Mockito.when(modelMapper.map(any(AppointmentDetailsDto.class), any())).thenReturn(getOptionalAppointment(false, false, false, false).orElse(null));
        Mockito.when(userRepository.findById(1)).thenReturn(getOptionalUser(false));
        Mockito.when(specialityRepository.existsByTitle(SPECIALITY_TITLE)).thenReturn(true);
        Mockito.when(specialityRepository.findByTitle(SPECIALITY_TITLE)).thenReturn(new Speciality(1, SPECIALITY_TITLE, null));
        Mockito.when(doctorRepository.findDoctorsIdWithSpeciality(1)).thenReturn(getDoctorView());
        Mockito.when(doctorRepository.findById(1)).thenReturn(getOptionalDoctor(true));
        assertThrows(NoDoctorAvailableExcepetion.class, () -> appointmentService.saveAppointment(APPOINTMENT_DETAILS_DTO, 1));
    }


    @Test
    void saveAppointmentSuccess() {

        //Expected
        Mockito.when(modelMapper.map(any(AppointmentDetailsDto.class), any())).thenReturn(getOptionalAppointment(false, false, false, false).orElse(null));
        Mockito.when(userRepository.findById(1)).thenReturn(getOptionalUser(false));
        Mockito.when(specialityRepository.existsByTitle(SPECIALITY_TITLE)).thenReturn(true);
        Mockito.when(specialityRepository.findByTitle(SPECIALITY_TITLE)).thenReturn(new Speciality(1, SPECIALITY_TITLE, new ArrayList<>(List.of(Objects.requireNonNull(getOptionalDoctor(false).orElse(null))))));
        Mockito.when(doctorRepository.findDoctorsIdWithSpeciality(1)).thenReturn(getDoctorView());
        Mockito.when(doctorRepository.findById(1)).thenReturn(getOptionalDoctor(false));
        Mockito.when(appointmentDoctorDtoConverter.freeDoctorToBookedDoctorConverter(new ArrayList<>(List.of(Objects.requireNonNull(getOptionalDoctor(false).orElse(null)))), new Appointment(1), LocalTime.now())).thenReturn(APPOINTMENT_DOCTOR_DTOS);
        Mockito.when(scheduleRepository.save(any(Schedule.class))).thenReturn(getOptionalSchedule(false, true).orElse(null));
        assertEquals(APPOINTMENT_DOCTOR_DTO1.getAppointmentId(), appointmentService.saveAppointment(APPOINTMENT_DETAILS_DTO, 1).getAppointmentId());
    }

    private List<DoctorView> getDoctorView() {
        DoctorView doctor1 = () -> 1;
        DoctorView doctor2 = () -> 2;

        return new ArrayList<>(Arrays.asList(doctor1, doctor2));

    }

    private Optional<Appointment> getOptionalAppointment(boolean isNull, boolean isCancelled, boolean afterCurrentTime, boolean withId) {
        if (isNull) {
            return Optional.empty();
        }
        Optional<Appointment> optionalAppointment = Optional.of(new Appointment(1, "ayurveda", LocalTime.parse("10:00", DateTimeFormatter.ofPattern("HH:mm"))
                , LocalDate.parse("17/12/2022", DateTimeFormatter.ofPattern("dd/MM/yyyy")), "random patient", "random issue", AppointmentStatus.AVAILABLE));
        if (withId)
            optionalAppointment.orElse(null).setAppointmentId(1);
        if (afterCurrentTime) {
            if (isCancelled) {
                optionalAppointment.orElse(null).setStatus(AppointmentStatus.CANCELLED);
                optionalAppointment.orElse(null).setEndTime(LocalTime.now().plusHours(1));
            } else
                optionalAppointment.orElse(null).setEndTime(LocalTime.now().plusHours(1));
        }
        if (isCancelled)
            optionalAppointment.orElse(null).setStatus(AppointmentStatus.CANCELLED);
        return optionalAppointment;
    }

    private Optional<Schedule> getOptionalSchedule(boolean isNull, boolean withId) {
        if (isNull) {
            return Optional.empty();
        }
        Optional<Schedule> optionalSchedule = Optional.of(new Schedule(LocalTime.now(), LocalDate.now(), getOptionalDoctor(false).orElse(null), getOptionalUser(false).orElse(null), getOptionalAppointment(false, false, true, true).orElse(null)));
        if (withId)
            optionalSchedule.orElse(null).setId(1);
        optionalSchedule.orElse(null).setAppointment(new Appointment(1));
        return optionalSchedule;
    }

    private Optional<Doctor> getOptionalDoctor(boolean isNull) {
        if (isNull) {
            return Optional.empty();
        }
        Doctor d = new Doctor();
        d.setDoctorId(1);
        d.setAppointments(new ArrayList<>(List.of(Objects.requireNonNull(getOptionalAppointment(false, false, false, true).orElse(null)))));
        return Optional.of(d);
    }

    private Optional<User> getOptionalUser(boolean isNull) {
        if (isNull)
            return Optional.empty();
        User u = new User();
        u.setAppointments(new ArrayList<>(List.of(Objects.requireNonNull(getOptionalAppointment(false, false, true, true).orElse(null)))));
        return Optional.of(u);
    }
}
