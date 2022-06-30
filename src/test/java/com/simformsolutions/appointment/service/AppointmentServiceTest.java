package com.simformsolutions.appointment.service;

import com.simformsolutions.appointment.converter.AppointmentDoctorDtoConverter;
import com.simformsolutions.appointment.dto.AppointmentDoctor;
import com.simformsolutions.appointment.dto.appointment.AppointmentDetails;
import com.simformsolutions.appointment.enums.AppointmentStatus;
import com.simformsolutions.appointment.excepetion.DoctorNotAvailableException;
import com.simformsolutions.appointment.excepetion.SpecialistNotFoundException;
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
import java.util.*;

import static com.simformsolutions.appointment.constants.AppointmentDetailsConstants.*;
import static com.simformsolutions.appointment.constants.AppointmentDoctorDetailsConstants.*;
import static com.simformsolutions.appointment.constants.DoctorDetailsConstants.DOCTOR_ID;
import static com.simformsolutions.appointment.constants.DoctorDetailsConstants.EXPERIENCE;
import static com.simformsolutions.appointment.constants.SpecialityConstants.SPECIALITY1;
import static com.simformsolutions.appointment.constants.SpecialityConstants.SPECIALITY_ID1;
import static com.simformsolutions.appointment.constants.UserInfoConstants.USER_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    private final ModelMapper modelMapper = mock(ModelMapper.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
    private final DoctorRepository doctorRepository = mock(DoctorRepository.class);
    private final ScheduleRepository scheduleRepository = mock(ScheduleRepository.class);
    private final SpecialityRepository specialityRepository = mock(SpecialityRepository.class);
    private final AppointmentDoctorDtoConverter appointmentDoctorDtoConverter = mock(AppointmentDoctorDtoConverter.class);
    private final AppointmentService appointmentService = new AppointmentService(scheduleRepository, doctorRepository, userRepository, appointmentDoctorDtoConverter, specialityRepository, appointmentRepository, modelMapper);
    AppointmentDoctor appointmentDoctor1 = new AppointmentDoctor(APPOINTMENT_ID1, DOCTOR_ID, DOCTOR_NAME, EXPERIENCE, SPECIALITY1, BOOKING_TIME, BOOKING_DATE, BOOKED_STATUS);
    AppointmentDetails appointmentDetails = new AppointmentDetails(SPECIALITY1, ISSUE, APPOINTMENT_DATE, PATIENT_NAME);
    AppointmentDoctor appointmentDoctor2 = new AppointmentDoctor(APPOINTMENT_ID2, DOCTOR_ID, DOCTOR_NAME, EXPERIENCE, SPECIALITY1, BOOKING_TIME, BOOKING_DATE, BOOKED_STATUS);
    List<AppointmentDoctor> listOfAppointmentDoctors = Arrays.asList(appointmentDoctor1, appointmentDoctor2);

    @Test
    void saveAppointmentAppointmentFailure() {
        //Expected
        Mockito.when(modelMapper.map(any(AppointmentDetails.class), any())).thenReturn(getOptionalAppointment(false, false).orElse(null));
        Mockito.when(userRepository.findById(USER_ID)).thenReturn(getNullOptionalUser());
        assertThrows(UserNotFoundException.class, () -> appointmentService.saveAppointment(appointmentDetails, USER_ID));
    }

    @Test
    void saveAppointmentSpecialityFailure() {
        //Expected
        Mockito.when(modelMapper.map(any(AppointmentDetails.class), any())).thenReturn(getOptionalAppointment(false, false).orElse(null));
        Mockito.when(userRepository.findById(USER_ID)).thenReturn(getOptionalUser());
        Mockito.when(specialityRepository.existsByTitle(SPECIALITY1)).thenReturn(false);
        assertThrows(SpecialityException.class, () -> appointmentService.saveAppointment(appointmentDetails, USER_ID));
    }

    @Test
    void saveAppointmentSpecialistFailure() {
        //Expected
        Mockito.when(modelMapper.map(any(AppointmentDetails.class), any())).thenReturn(getOptionalAppointment(false, false).orElse(null));
        Mockito.when(userRepository.findById(USER_ID)).thenReturn(getOptionalUser());
        Mockito.when(specialityRepository.existsByTitle(SPECIALITY1)).thenReturn(true);
        Mockito.when(specialityRepository.findByTitle(SPECIALITY1)).thenReturn(new Speciality(1, SPECIALITY1, null));
        Mockito.when(doctorRepository.findDoctorsIdWithSpeciality(SPECIALITY_ID1)).thenReturn(new ArrayList<>());
        assertThrows(SpecialistNotFoundException.class, () -> appointmentService.saveAppointment(appointmentDetails, USER_ID));
    }

    @Test
    void saveAppointmentDoctorFailure() {
        //Expected
        Mockito.when(modelMapper.map(any(AppointmentDetails.class), any())).thenReturn(getOptionalAppointment(false, false).orElse(null));
        Mockito.when(userRepository.findById(USER_ID)).thenReturn(getOptionalUser());
        Mockito.when(specialityRepository.existsByTitle(SPECIALITY1)).thenReturn(true);
        Mockito.when(specialityRepository.findByTitle(SPECIALITY1)).thenReturn(new Speciality(SPECIALITY_ID1, SPECIALITY1, null));
        Mockito.when(doctorRepository.findDoctorsIdWithSpeciality(SPECIALITY_ID1)).thenReturn(getDoctorView());
        Mockito.when(doctorRepository.findById(SPECIALITY_ID1)).thenReturn(getNullOptionalDoctor());
        assertThrows(DoctorNotAvailableException.class, () -> appointmentService.saveAppointment(appointmentDetails, USER_ID));
    }

    @Test
    void saveAppointmentSuccess() {
        //Expected
        Mockito.when(modelMapper.map(any(AppointmentDetails.class), any())).thenReturn(getOptionalAppointment(false, false).orElse(null));
        Mockito.when(userRepository.findById(USER_ID)).thenReturn(getOptionalUser());
        Mockito.when(specialityRepository.existsByTitle(SPECIALITY1)).thenReturn(true);
        Mockito.when(specialityRepository.findByTitle(SPECIALITY1)).thenReturn(new Speciality(SPECIALITY_ID1, SPECIALITY1, new ArrayList<>(List.of(Objects.requireNonNull(getOptionalDoctor().orElse(null))))));
        Mockito.when(doctorRepository.findDoctorsIdWithSpeciality(SPECIALITY_ID1)).thenReturn(getDoctorView());
        Mockito.when(doctorRepository.findById(DOCTOR_ID)).thenReturn(getOptionalDoctor());
        Mockito.when(appointmentDoctorDtoConverter.freeDoctorToBookedDoctorConverter(new ArrayList<>(List.of(Objects.requireNonNull(getOptionalDoctor().orElse(null)))), new Appointment(APPOINTMENT_ID1), LocalTime.now())).thenReturn(listOfAppointmentDoctors);
        Mockito.when(scheduleRepository.save(any(Schedule.class))).thenReturn(getOptionalScheduleWithId().orElse(null));
        assertEquals(appointmentDoctor1.getAppointmentId(), appointmentService.saveAppointment(appointmentDetails, USER_ID).getAppointmentId());
    }

    private List<DoctorView> getDoctorView() {
        DoctorView doctor1 = () -> 1;
        DoctorView doctor2 = () -> 2;
        return new ArrayList<>(Arrays.asList(doctor1, doctor2));
    }

    private Optional<Appointment> getOptionalAppointment(boolean isAfterCurrentTime, boolean hasId) {
        Optional<Appointment> optionalAppointment = Optional.of(new Appointment(APPOINTMENT_ID1, SPECIALITY1, BOOKING_TIME, BOOKING_DATE, PATIENT_NAME, ISSUE, AppointmentStatus.AVAILABLE));
        if (hasId) optionalAppointment.orElse(null).setAppointmentId(APPOINTMENT_ID1);
        optionalAppointment.orElse(null).setStatus(AppointmentStatus.CANCELLED);
        if (isAfterCurrentTime) {
            optionalAppointment.orElse(null).setEndTime(LocalTime.now().plusHours(1));
        } else optionalAppointment.orElse(null).setEndTime(LocalTime.now().plusHours(1));
        return optionalAppointment;
    }

    private Optional<Schedule> getOptionalScheduleWithId() {
        Optional<Schedule> optionalSchedule = Optional.of(new Schedule(LocalTime.now(), LocalDate.now(), getOptionalDoctor().orElse(null), getOptionalUser().orElse(null), getOptionalAppointment(true, true).orElse(null)));
        optionalSchedule.orElse(null).setId(1);
        optionalSchedule.orElse(null).setAppointment(new Appointment(APPOINTMENT_ID1));
        return optionalSchedule;
    }

    private Optional<Doctor> getOptionalDoctor() {
        Doctor doctor = new Doctor();
        doctor.setDoctorId(DOCTOR_ID);
        doctor.setAppointments(new ArrayList<>(List.of(Objects.requireNonNull(getOptionalAppointment(false, true).orElse(null)))));
        return Optional.of(doctor);

    }

    private Optional<User> getOptionalUser() {
        User user = new User();
        user.setAppointments(new ArrayList<>(List.of(Objects.requireNonNull(getOptionalAppointment(true, true).orElse(null)))));
        return Optional.of(user);
    }

    private Optional<Doctor> getNullOptionalDoctor() {
        return Optional.empty();
    }

    private Optional<User> getNullOptionalUser() {
        return Optional.empty();
    }

}
