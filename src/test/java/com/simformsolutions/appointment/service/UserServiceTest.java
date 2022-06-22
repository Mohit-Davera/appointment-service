package com.simformsolutions.appointment.service;

import com.simformsolutions.appointment.converter.AppointmentDoctorDtoConverter;
import com.simformsolutions.appointment.dto.AppointmentDoctorDto;
import com.simformsolutions.appointment.dto.appointment.AppointmentDetailsDto;
import com.simformsolutions.appointment.dto.user.UserDetailsDto;
import com.simformsolutions.appointment.enums.AppointmentStatus;
import com.simformsolutions.appointment.excepetion.NoAppointmentFoundException;
import com.simformsolutions.appointment.excepetion.StatusChangeException;
import com.simformsolutions.appointment.model.Appointment;
import com.simformsolutions.appointment.model.User;
import com.simformsolutions.appointment.repository.AppointmentRepository;
import com.simformsolutions.appointment.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;

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
class UserServiceTest {

    static final UserDetailsDto USER_DETAILS_DTO = new UserDetailsDto("Mohit D", "mohit@gmail.com", "0123456789", "password");
    static final AppointmentDoctorDto APPOINTMENT_DOCTOR_DTO1 = new AppointmentDoctorDto(1, 1, "Ravi D", 2, "ayurveda", LocalTime.parse("10:00", DateTimeFormatter.ofPattern("HH:mm")), LocalDate.parse("17/12/2022", DateTimeFormatter.ofPattern("dd/MM/yyyy")), "BOOKED");
    static final AppointmentDetailsDto APPOINTMENT_DETAILS_DTO = new AppointmentDetailsDto("ayurveda", "random issue", LocalDate.parse("17/12/2022", DateTimeFormatter.ofPattern("dd/MM/yyyy")), "random user");
    static final AppointmentDoctorDto APPOINTMENT_DOCTOR_DTO2 = new AppointmentDoctorDto(2, 1, "Ravi D", 2, "ayurveda", LocalTime.parse("11:00", DateTimeFormatter.ofPattern("HH:mm")), LocalDate.parse("18/12/2022", DateTimeFormatter.ofPattern("dd/MM/yyyy")), "BOOKED");
    static final List<AppointmentDoctorDto> APPOINTMENT_DOCTOR_DTOS = new ArrayList<>(Arrays.asList(APPOINTMENT_DOCTOR_DTO1, APPOINTMENT_DOCTOR_DTO2));

    @MockBean
    ModelMapper modelMapper;

    @MockBean
    UserRepository userRepository;

    @MockBean
    AppointmentRepository appointmentRepository;

    @MockBean
    AppointmentDoctorDtoConverter appointmentDoctorDtoConverter;

    @Autowired
    UserService userService;

    @MockBean
    AppointmentService appointmentService;

    @Test
    void addUserSuccess() {

        //Expected
        Mockito.when(modelMapper.map(any(UserDetailsDto.class), any(Class.class))).thenReturn(getUserDetails(false));
        Mockito.when(userRepository.save(any(User.class))).thenReturn(getUserDetails(true));

        //Actual Data
        UserDetailsDto userDetailsDto1 = userService.addUser(getUserDetailsDto(false));
        assertEquals(getUserDetailsDto(true).getUserId(), userDetailsDto1.getUserId());
    }

    @Test
    void addUserConflict() {

        //Expected
        Mockito.when(modelMapper.map(any(UserDetailsDto.class), any(Class.class))).thenReturn(getUserDetails(false));
        Mockito.when(userRepository.save(any(User.class))).thenThrow(DataIntegrityViolationException.class);
        //Actual Data
        assertThrows(DataIntegrityViolationException.class, () -> userService.addUser(getUserDetailsDto(false)));
    }

    @Test
    void getAppointmentsSuccess() {
        Mockito.when(appointmentRepository.findDetailsOfAppointments(1)).thenReturn(getListTuple());
        Mockito.when(appointmentDoctorDtoConverter.tuplesToAppointmentDoctorConverter(Mockito.anyList())).thenReturn(APPOINTMENT_DOCTOR_DTOS);
        System.out.println(APPOINTMENT_DOCTOR_DTOS);
        assertEquals(APPOINTMENT_DOCTOR_DTOS, userService.getAppointments(1));
    }

    @Test
    void rescheduleAppointmentsAppointmentFailure() {
        Mockito.when(appointmentRepository.findById(1)).thenReturn(getOptionalAppointment(true, false, false, false));

        assertThrows(NoAppointmentFoundException.class, () -> userService.rescheduleAppointment(1, 1, "2"));
    }

    @Test
    void rescheduleAppointmentsCancelledStatusFailure() {
        Mockito.when(appointmentRepository.findById(1)).thenReturn(getOptionalAppointment(false, true, true, false));
        assertThrows(StatusChangeException.class, () -> userService.rescheduleAppointment(1, 1, "2"));
    }

    @Test
    void rescheduleAppointmentsBeforeStatusFailure() {
        Mockito.when(appointmentRepository.findById(1)).thenReturn(getOptionalAppointment(false, false, false, false));

        assertThrows(StatusChangeException.class, () -> userService.rescheduleAppointment(1, 1, "2"));
    }

    @Test
    void rescheduleAppointmentsWithDaysSuccess() {
        Mockito.when(appointmentRepository.findById(1)).thenReturn(getOptionalAppointment(false, false, true, false));
        Mockito.when(modelMapper.map(any(UserDetailsDto.class), any(Class.class))).thenReturn(APPOINTMENT_DETAILS_DTO);
        Mockito.when(appointmentService.saveAppointment(APPOINTMENT_DETAILS_DTO, 1)).thenReturn(APPOINTMENT_DOCTOR_DTO1);
        Mockito.when(appointmentRepository.save(getOptionalAppointment(false, false, true, false).get())).thenReturn(getOptionalAppointment(false, false, true, true).get());
        Mockito.when(appointmentRepository.findDetailsOfAppointment(1)).thenReturn(getListTuple());
        Mockito.when(appointmentDoctorDtoConverter.tuplesToAppointmentDoctorConverter(anyList())).thenReturn(APPOINTMENT_DOCTOR_DTOS);

        assertEquals(APPOINTMENT_DOCTOR_DTOS.get(0), userService.rescheduleAppointment(1, 1, "2"));
    }


    UserDetailsDto getUserDetailsDto(boolean withId) {
        if (withId) {
            USER_DETAILS_DTO.setUserId(1);
            return USER_DETAILS_DTO;
        }
        return USER_DETAILS_DTO;
    }

    User getUserDetails(boolean withId) {
        if (withId)
            return new User(1, "Mohit D", "mohit@gmail.com", "0123456789", "password");
        return new User("Mohit D", "mohit@gmail.com", "0123456789", "password");
    }

    List<Tuple> getListTuple() {
        Tuple mockedTuple1 = Mockito.mock(Tuple.class);
        Tuple mockedTuple2 = Mockito.mock(Tuple.class);
        List<Tuple> tuples = new ArrayList<Tuple>();
        tuples.add(0, mockedTuple1);
        tuples.add(1, mockedTuple2);
        return tuples;
    }

    Optional<Appointment> getOptionalAppointment(boolean isNull, boolean isCancelled, boolean afterCurrentTime, boolean withId) {
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
}
