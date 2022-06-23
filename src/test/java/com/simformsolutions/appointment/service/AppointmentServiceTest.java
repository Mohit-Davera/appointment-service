package com.simformsolutions.appointment.service;

import com.simformsolutions.appointment.converter.AppointmentDoctorDtoConverter;
import com.simformsolutions.appointment.dto.AppointmentDoctorDto;
import com.simformsolutions.appointment.dto.appointment.AppointmentDetailsDto;
import com.simformsolutions.appointment.dto.user.UserDetailsDto;
import com.simformsolutions.appointment.enums.AppointmentStatus;
import com.simformsolutions.appointment.excepetion.NoDoctorAvailableExcepetion;
import com.simformsolutions.appointment.excepetion.NoSpecialistFoundExcpetion;
import com.simformsolutions.appointment.excepetion.SpecialityException;
import com.simformsolutions.appointment.excepetion.UserNotFoundException;
import com.simformsolutions.appointment.model.*;
import com.simformsolutions.appointment.projection.DoctorView;
import com.simformsolutions.appointment.repository.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.persistence.Tuple;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;

@SpringBootTest
@AutoConfigureMockMvc
class AppointmentServiceTest {

    static final UserDetailsDto USER_DETAILS_DTO = new UserDetailsDto("Mohit D", "mohit@gmail.com", "0123456789", "password");
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
        Mockito.when(modelMapper.map(any(AppointmentDetailsDto.class), any(Class.class))).thenReturn(getOptionalAppointment(false, false, false, false).get());
        Mockito.when(userRepository.findById(1)).thenReturn(getOptionalUser(true));
        assertThrows(UserNotFoundException.class, () -> appointmentService.saveAppointment(APPOINTMENT_DETAILS_DTO, 1));
    }

    @Test
    void saveAppointmentSpecialityFailure() {

        //Expected
        Mockito.when(modelMapper.map(any(AppointmentDetailsDto.class), any(Class.class))).thenReturn(getOptionalAppointment(false, false, false, false).get());
        Mockito.when(userRepository.findById(1)).thenReturn(getOptionalUser(false));
        Mockito.when(specialityRepository.existsByTitle(SPECIALITY_TITLE)).thenReturn(false);
        assertThrows(SpecialityException.class, () -> appointmentService.saveAppointment(APPOINTMENT_DETAILS_DTO, 1));
    }

    @Test
    void saveAppointmentSpecialistFailure() {

        //Expected
        Mockito.when(modelMapper.map(any(AppointmentDetailsDto.class), any(Class.class))).thenReturn(getOptionalAppointment(false, false, false, false).get());
        Mockito.when(userRepository.findById(1)).thenReturn(getOptionalUser(false));
        Mockito.when(specialityRepository.existsByTitle(SPECIALITY_TITLE)).thenReturn(true);
        Mockito.when(specialityRepository.findByTitle(SPECIALITY_TITLE)).thenReturn(new Speciality(1, SPECIALITY_TITLE, null));
        Mockito.when(doctorRepository.findDoctorsIdWithSpeciality(1)).thenReturn(new ArrayList<>());
        assertThrows(NoSpecialistFoundExcpetion.class, () -> appointmentService.saveAppointment(APPOINTMENT_DETAILS_DTO, 1));
    }

    @Test
    void saveAppointmentDoctorFailure() {

        //Expected
        Mockito.when(modelMapper.map(any(AppointmentDetailsDto.class), any(Class.class))).thenReturn(getOptionalAppointment(false, false, false, false).get());
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
        Mockito.when(modelMapper.map(any(AppointmentDetailsDto.class), any(Class.class))).thenReturn(getOptionalAppointment(false, false, false, false).get());
        Mockito.when(userRepository.findById(1)).thenReturn(getOptionalUser(false));
        Mockito.when(specialityRepository.existsByTitle(SPECIALITY_TITLE)).thenReturn(true);
        Mockito.when(specialityRepository.findByTitle(SPECIALITY_TITLE)).thenReturn(new Speciality(1, SPECIALITY_TITLE, new ArrayList<>(Arrays.asList(getOptionalDoctor(false).get()))));
        Mockito.when(doctorRepository.findDoctorsIdWithSpeciality(1)).thenReturn(getDoctorView());
        Mockito.when(doctorRepository.findById(1)).thenReturn(getOptionalDoctor(false));
        Mockito.when(appointmentDoctorDtoConverter.freeDoctorToBookedDoctorConverter(new ArrayList<>(Arrays.asList(getOptionalDoctor(false).get())), new Appointment(1), LocalTime.now())).thenReturn(APPOINTMENT_DOCTOR_DTOS);
        Mockito.when(scheduleRepository.save(any(Schedule.class))).thenReturn(getOptionalSchedule(false,true).get());
        assertEquals(APPOINTMENT_DOCTOR_DTO1.getAppointmentId(),appointmentService.saveAppointment(APPOINTMENT_DETAILS_DTO, 1).getAppointmentId());
    }

    private List<DoctorView> getDoctorView() {
        DoctorView doctor1 = new DoctorView() {@Override public int getDoctorId() {return 1;}};
        DoctorView doctor2 = new DoctorView() {@Override public int getDoctorId() {return 2;}};

        return new ArrayList<>(Arrays.asList(doctor1,doctor2));

    }

    private List<Doctor> getDoctors() {
        Appointment appointment = Mockito.mock(Appointment.class);
        Appointment appointment1 = Mockito.mock(Appointment.class);
        Doctor d1 = new Doctor(1);
        d1.addAppointment(appointment);
        Doctor d2 = new Doctor(2);
        d2.addAppointment(appointment1);
        return new ArrayList<>(Arrays.asList(d1, d2));

    }

    private UserDetailsDto getUserDetailsDto(boolean withId) {
        if (withId) {
            USER_DETAILS_DTO.setUserId(1);
            return USER_DETAILS_DTO;
        }
        return USER_DETAILS_DTO;
    }

    private User getUserDetails(boolean withId) {
        if (withId)
            return new User(1, "Mohit D", "mohit@gmail.com", "0123456789", "password");
        return new User("Mohit D", "mohit@gmail.com", "0123456789", "password");
    }

    private List<Tuple> getListTuple() {
        Tuple mockedTuple1 = Mockito.mock(Tuple.class);
        Tuple mockedTuple2 = Mockito.mock(Tuple.class);
        List<Tuple> tuples = new ArrayList<Tuple>();
        tuples.add(0, mockedTuple1);
        tuples.add(1, mockedTuple2);
        return tuples;
    }

    private Optional<Appointment> getOptionalAppointment(boolean isNull, boolean isCancelled, boolean afterCurrentTime, boolean withId) {
        if (isNull) {
            return Optional.empty();
        }
        Optional<Appointment> optionalAppointment = Optional.of(new Appointment(1, "ayurveda", LocalTime.parse("10:00", DateTimeFormatter.ofPattern("HH:mm"))
                , LocalDate.parse("17/12/2022", DateTimeFormatter.ofPattern("dd/MM/yyyy")), "random patient", "random issue", AppointmentStatus.AVAILABLE));
        if (withId)
            optionalAppointment.get().setAppointmentId(1);
        if (afterCurrentTime) {
            if (isCancelled) {
                optionalAppointment.get().setStatus(AppointmentStatus.CANCELLED);
                optionalAppointment.get().setEndTime(LocalTime.now().plusHours(1));
            } else
                optionalAppointment.get().setEndTime(LocalTime.now().plusHours(1));
        }
        if (isCancelled)
            optionalAppointment.get().setStatus(AppointmentStatus.CANCELLED);
        return optionalAppointment;
    }

    private Optional<Schedule> getOptionalSchedule(boolean isNull, boolean withId) {
        if (isNull) {
            return Optional.empty();
        }
        Optional<Schedule> optionalSchedule = Optional.of(new Schedule(LocalTime.now(), LocalDate.now(), getOptionalDoctor(false).get(), getOptionalUser(false).get(), getOptionalAppointment(false, false, true, true).get()));
        if (withId)
            optionalSchedule.get().setId(1);
        optionalSchedule.get().setAppointment(new Appointment(1));
        return optionalSchedule;
    }

    private Optional<Doctor> getOptionalDoctor(boolean isNull) {
        if (isNull) {
            return Optional.empty();
        }
        Doctor d = new Doctor();
        d.setDoctorId(1);
        d.setAppointments(new ArrayList<>(List.of(getOptionalAppointment(false, false, false, true).get())));
        return Optional.of(d);
    }

    private Optional<User> getOptionalUser(boolean isNull) {
        if (isNull)
            return Optional.empty();
        User u = new User();
        u.setAppointments(new ArrayList<>(List.of(getOptionalAppointment(false, false, true, true).get())));
        return Optional.of(u);
    }
}
