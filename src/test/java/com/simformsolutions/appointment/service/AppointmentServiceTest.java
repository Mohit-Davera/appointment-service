package com.simformsolutions.appointment.service;

import com.simformsolutions.appointment.converter.AppointmentDoctorDtoConverter;
import com.simformsolutions.appointment.dto.AppointmentDoctorDto;
import com.simformsolutions.appointment.dto.appointment.AppointmentDetailsDto;
import com.simformsolutions.appointment.enums.AppointmentStatus;
import com.simformsolutions.appointment.excepetion.NoDoctorAvailableExcepetion;
import com.simformsolutions.appointment.excepetion.NoSpecialistFoundException;
import com.simformsolutions.appointment.excepetion.SpecialityException;
import com.simformsolutions.appointment.excepetion.UserNotFoundException;
import com.simformsolutions.appointment.model.*;
import com.simformsolutions.appointment.projection.DoctorView;
import com.simformsolutions.appointment.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    static final AppointmentDoctorDto APPOINTMENT_DOCTOR_DTO1 = new AppointmentDoctorDto(1, 1, "Ravi D", 2, "ayurveda", LocalTime.now(), LocalDate.now(), "BOOKED");
    static final AppointmentDetailsDto APPOINTMENT_DETAILS_DTO = new AppointmentDetailsDto("ayurveda", "random issue", LocalDate.now(), "random user");
    static final AppointmentDoctorDto APPOINTMENT_DOCTOR_DTO2 = new AppointmentDoctorDto(2, 1, "Ravi D", 2, "ayurveda", LocalTime.now(), LocalDate.now(), "BOOKED");
    static final List<AppointmentDoctorDto> APPOINTMENT_DOCTOR_DTOS = Arrays.asList(APPOINTMENT_DOCTOR_DTO1, APPOINTMENT_DOCTOR_DTO2);

    static final String SPECIALITY = "ayurveda";

    private final ModelMapper modelMapper = mock(ModelMapper.class);


    private final UserRepository userRepository = mock(UserRepository.class);


    private final AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);

    private final DoctorRepository doctorRepository = mock(DoctorRepository.class);


    private final ScheduleRepository scheduleRepository = mock(ScheduleRepository.class);


    private final SpecialityRepository specialityRepository = mock(SpecialityRepository.class);

    private final AppointmentDoctorDtoConverter appointmentDoctorDtoConverter = mock(AppointmentDoctorDtoConverter.class);

    private final AppointmentService appointmentService = new AppointmentService(scheduleRepository, doctorRepository, userRepository, appointmentDoctorDtoConverter, specialityRepository, appointmentRepository, modelMapper);


    @Test
    void saveAppointmentAppointmentFailure() {

        //Expected
        Mockito.when(modelMapper.map(any(AppointmentDetailsDto.class), any())).thenReturn(getOptionalAppointment(false, false).orElse(null));
        Mockito.when(userRepository.findById(1)).thenReturn(getNullOptionalUser());
        assertThrows(UserNotFoundException.class, () -> appointmentService.saveAppointment(APPOINTMENT_DETAILS_DTO, 1));
    }

    @Test
    void saveAppointmentSpecialityFailure() {

        //Expected
        Mockito.when(modelMapper.map(any(AppointmentDetailsDto.class), any())).thenReturn(getOptionalAppointment(false, false).orElse(null));
        Mockito.when(userRepository.findById(1)).thenReturn(getOptionalUser());
        Mockito.when(specialityRepository.existsByTitle(SPECIALITY)).thenReturn(false);
        assertThrows(SpecialityException.class, () -> appointmentService.saveAppointment(APPOINTMENT_DETAILS_DTO, 1));
    }

    @Test
    void saveAppointmentSpecialistFailure() {

        //Expected
        Mockito.when(modelMapper.map(any(AppointmentDetailsDto.class), any())).thenReturn(getOptionalAppointment(false, false).orElse(null));
        Mockito.when(userRepository.findById(1)).thenReturn(getOptionalUser());
        Mockito.when(specialityRepository.existsByTitle(SPECIALITY)).thenReturn(true);
        Mockito.when(specialityRepository.findByTitle(SPECIALITY)).thenReturn(new Speciality(1, SPECIALITY, null));
        Mockito.when(doctorRepository.findDoctorsIdWithSpeciality(1)).thenReturn(new ArrayList<>());
        assertThrows(NoSpecialistFoundException.class, () -> appointmentService.saveAppointment(APPOINTMENT_DETAILS_DTO, 1));
    }

    @Test
    void saveAppointmentDoctorFailure() {

        //Expected
        Mockito.when(modelMapper.map(any(AppointmentDetailsDto.class), any())).thenReturn(getOptionalAppointment(false, false).orElse(null));
        Mockito.when(userRepository.findById(1)).thenReturn(getOptionalUser());
        Mockito.when(specialityRepository.existsByTitle(SPECIALITY)).thenReturn(true);
        Mockito.when(specialityRepository.findByTitle(SPECIALITY)).thenReturn(new Speciality(1, SPECIALITY, null));
        Mockito.when(doctorRepository.findDoctorsIdWithSpeciality(1)).thenReturn(getDoctorView());
        Mockito.when(doctorRepository.findById(1)).thenReturn(getNullOptionalDoctor());
        assertThrows(NoDoctorAvailableExcepetion.class, () -> appointmentService.saveAppointment(APPOINTMENT_DETAILS_DTO, 1));
    }


    @Test
    void saveAppointmentSuccess() {

        //Expected
        Mockito.when(modelMapper.map(any(AppointmentDetailsDto.class), any())).thenReturn(getOptionalAppointment(false, false).orElse(null));
        Mockito.when(userRepository.findById(1)).thenReturn(getOptionalUser());
        Mockito.when(specialityRepository.existsByTitle(SPECIALITY)).thenReturn(true);
        Mockito.when(specialityRepository.findByTitle(SPECIALITY)).thenReturn(new Speciality(1, SPECIALITY, new ArrayList<>(List.of(Objects.requireNonNull(getOptionalDoctor().orElse(null))))));
        Mockito.when(doctorRepository.findDoctorsIdWithSpeciality(1)).thenReturn(getDoctorView());
        Mockito.when(doctorRepository.findById(1)).thenReturn(getOptionalDoctor());
        Mockito.when(appointmentDoctorDtoConverter.freeDoctorToBookedDoctorConverter(new ArrayList<>(List.of(Objects.requireNonNull(getOptionalDoctor().orElse(null)))), new Appointment(1), LocalTime.now())).thenReturn(APPOINTMENT_DOCTOR_DTOS);
        Mockito.when(scheduleRepository.save(any(Schedule.class))).thenReturn(getOptionalScheduleWithId().orElse(null));
        assertEquals(APPOINTMENT_DOCTOR_DTO1.getAppointmentId(), appointmentService.saveAppointment(APPOINTMENT_DETAILS_DTO, 1).getAppointmentId());
    }

    private List<DoctorView> getDoctorView() {
        DoctorView doctor1 = () -> 1;
        DoctorView doctor2 = () -> 2;
        return new ArrayList<>(Arrays.asList(doctor1, doctor2));
    }

    private Optional<Appointment> getOptionalAppointment(boolean afterCurrentTime, boolean withId) {
        Optional<Appointment> optionalAppointment = Optional.of(new Appointment(1, "ayurveda", LocalTime.parse("10:00", DateTimeFormatter.ofPattern("HH:mm")), LocalDate.parse("17/12/2022", DateTimeFormatter.ofPattern("dd/MM/yyyy")), "random patient", "random issue", AppointmentStatus.AVAILABLE));
        if (withId) optionalAppointment.orElse(null).setAppointmentId(1);
        optionalAppointment.orElse(null).setStatus(AppointmentStatus.CANCELLED);
        if (afterCurrentTime) {

            optionalAppointment.orElse(null).setEndTime(LocalTime.now().plusHours(1));
        } else optionalAppointment.orElse(null).setEndTime(LocalTime.now().plusHours(1));
        return optionalAppointment;
    }

    private Optional<Schedule> getOptionalScheduleWithId() {
        Optional<Schedule> optionalSchedule = Optional.of(new Schedule(LocalTime.now(), LocalDate.now(), getOptionalDoctor().orElse(null), getOptionalUser().orElse(null), getOptionalAppointment(true, true).orElse(null)));
        optionalSchedule.orElse(null).setId(1);
        optionalSchedule.orElse(null).setAppointment(new Appointment(1));
        return optionalSchedule;
    }

    private Optional<Doctor> getOptionalDoctor() {
        Doctor d = new Doctor();
        d.setDoctorId(1);
        d.setAppointments(new ArrayList<>(List.of(Objects.requireNonNull(getOptionalAppointment(false, true).orElse(null)))));
        return Optional.of(d);

    }

    private Optional<User> getOptionalUser() {
        User u = new User();
        u.setAppointments(new ArrayList<>(List.of(Objects.requireNonNull(getOptionalAppointment(true, true).orElse(null)))));
        return Optional.of(u);
    }

    private Optional<Doctor> getNullOptionalDoctor() {
        return Optional.empty();
    }

    private Optional<User> getNullOptionalUser() {
        return Optional.empty();
    }

}
