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
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;

@SpringBootTest
@AutoConfigureMockMvc
class UserServiceTest {

    static final UserDetailsDto USER_DETAILS_DTO = new UserDetailsDto("Mohit D", "mohit@gmail.com", "0123456789", "password");
    static final AppointmentDoctorDto APPOINTMENT_DOCTOR_DTO1 = new AppointmentDoctorDto(1, 1, "Ravi D", 2, "ayurveda", LocalTime.now(), LocalDate.now(), "BOOKED");
    static final AppointmentDetailsDto APPOINTMENT_DETAILS_DTO = new AppointmentDetailsDto("ayurveda", "random issue", LocalDate.now(), "random user");
    static final AppointmentDoctorDto APPOINTMENT_DOCTOR_DTO2 = new AppointmentDoctorDto(2, 1, "Ravi D", 2, "ayurveda", LocalTime.now(), LocalDate.now(), "BOOKED");
    static final List<AppointmentDoctorDto> APPOINTMENT_DOCTOR_DTOS = new ArrayList<>(Arrays.asList(APPOINTMENT_DOCTOR_DTO1, APPOINTMENT_DOCTOR_DTO2));

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
    private AppointmentDoctorDtoConverter appointmentDoctorDtoConverter;

    @Autowired
    private UserService userService;

    @MockBean
    private AppointmentService appointmentService;

    UserServiceTest() {
    }

    @Test
    void addUserSuccess() {

        //Expected
        Mockito.when(modelMapper.map(any(UserDetailsDto.class), any())).thenReturn(getUserDetails(false));
        Mockito.when(userRepository.save(any(User.class))).thenReturn(getUserDetails(true));

        //Actual Data
        UserDetailsDto userDetailsDto1 = userService.addUser(getUserDetailsDto(false));
        assertEquals(getUserDetailsDto(true).getUserId(), userDetailsDto1.getUserId());
    }

    @Test
    void addUserConflict() {

        //Expected
        Mockito.when(modelMapper.map(any(UserDetailsDto.class), any())).thenReturn(getUserDetails(false));
        Mockito.when(userRepository.save(any(User.class))).thenThrow(DataIntegrityViolationException.class);
        //Actual Data
        UserDetailsDto userDetailsDto = getUserDetailsDto(false);
        assertThrows(DataIntegrityViolationException.class, () -> userService.addUser(userDetailsDto));
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
        Mockito.when(modelMapper.map(any(UserDetailsDto.class), any())).thenReturn(APPOINTMENT_DETAILS_DTO);
        Mockito.when(appointmentService.saveAppointment(APPOINTMENT_DETAILS_DTO, 1)).thenReturn(APPOINTMENT_DOCTOR_DTO1);
        Mockito.when(appointmentRepository.save(Objects.requireNonNull(getOptionalAppointment(false, false, true, false).orElse(null)))).thenReturn(getOptionalAppointment(false, false, true, true).orElse(null));
        Mockito.when(appointmentRepository.findDetailsOfAppointment(1)).thenReturn(getListTuple());
        Mockito.when(appointmentDoctorDtoConverter.tuplesToAppointmentDoctorConverter(anyList())).thenReturn(APPOINTMENT_DOCTOR_DTOS);

        assertEquals(APPOINTMENT_DOCTOR_DTOS.get(0), userService.rescheduleAppointment(1, 1, "2"));
    }

    @Test
    void cancelAppointmentFailure() {
        Mockito.when(appointmentRepository.findById(1)).thenReturn(getOptionalAppointment(true, false, true, false));
        assertThrows(NoAppointmentFoundException.class, () -> userService.cancelAppointment(1));
    }

    @Test
    void cancelAppointmentSuccess() {
        Optional<Appointment> optionalAppointment = getOptionalAppointment(false, false, true, false);
        Mockito.when(appointmentRepository.findById(1)).thenReturn(optionalAppointment);
        assert optionalAppointment.orElse(null) != null;
        Mockito.when(appointmentRepository.save(optionalAppointment.orElse(null))).thenReturn(optionalAppointment.orElse(null));
        Mockito.when(appointmentRepository.findDetailsOfAppointment(1)).thenReturn(getListTuple());
        Mockito.when(appointmentDoctorDtoConverter.tuplesToAppointmentDoctorConverter(anyList())).thenReturn(APPOINTMENT_DOCTOR_DTOS);

        assertEquals(APPOINTMENT_DOCTOR_DTOS.get(0), userService.cancelAppointment(1));

    }

    @Test
    void getAvailableDoctorsFailure() {
        Mockito.when(appointmentRepository.findById(1)).thenReturn(getOptionalAppointment(true, false, true, false));
        assertThrows(NoAppointmentFoundException.class, () -> userService.getAvailableDoctors(1, 1));
    }

    @Test
    void getAvailableDoctorsSuccess() {
        Optional<Appointment> optionalAppointment = getOptionalAppointment(false, false, true, false);
        Mockito.when(appointmentRepository.findById(1)).thenReturn(optionalAppointment);
        assert optionalAppointment.orElse(null) != null;
        Mockito.when(appointmentService.bookAppointmentAgain(optionalAppointment.orElse(null), 1)).thenReturn(APPOINTMENT_DOCTOR_DTOS);

        assertEquals(APPOINTMENT_DOCTOR_DTOS, userService.getAvailableDoctors(1, 1));
    }

    @Test
    void changeDoctorFailure() {
        Mockito.when(scheduleRepository.getScheduleFromAppointmentId(APPOINTMENT_DOCTOR_DTO1.getAppointmentId())).thenReturn(getOptionalSchedule(true,false));
        assertThrows(ScheduleNotFoundException.class, () -> userService.changeDoctor(APPOINTMENT_DOCTOR_DTO1, 1));
    }

    @Test
    void changeDoctorSuccess() {
        Optional<Schedule> optionalSchedule = Optional.of(new Schedule(LocalTime.now(), LocalDate.now(), getOptionalDoctor().orElse(null), getOptionalUser().orElse(null), new Appointment(1)));
        optionalSchedule.orElse(null).setId(1);
        optionalSchedule.orElse(null).setAppointment(new Appointment(1));
        Schedule schedule = optionalSchedule.orElse(null);

        Mockito.when(scheduleRepository.getScheduleFromAppointmentId(APPOINTMENT_DOCTOR_DTO1.getAppointmentId())).thenReturn(getOptionalSchedule(false,true));
        Mockito.when(userRepository.findById(1)).thenReturn(getOptionalUser());
        Mockito.when(doctorRepository.findById(1)).thenReturn(getOptionalDoctor());
        Mockito.when(appointmentRepository.findById(1)).thenReturn(Optional.of(new Appointment(1)));
        Mockito.when(scheduleRepository.save(any(Schedule.class))).thenReturn(schedule);

        assertEquals(APPOINTMENT_DOCTOR_DTO1,userService.changeDoctor(APPOINTMENT_DOCTOR_DTO1,1));
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
        List<Tuple> tuples = new ArrayList<>();
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
        Optional<Schedule> optionalSchedule = Optional.of(new Schedule(LocalTime.now(), LocalDate.now(), getOptionalDoctor().orElse(null), getOptionalUser().orElse(null), getOptionalAppointment(false, false, true, true).orElse(null)));
        if (withId)
            optionalSchedule.orElse(null).setId(1);
        optionalSchedule.orElse(null).setAppointment(new Appointment(1));
        return optionalSchedule;
    }
    private Optional<Doctor> getOptionalDoctor(){
        Doctor d = new Doctor();
        d.setAppointments(new ArrayList<>(List.of(Objects.requireNonNull(getOptionalAppointment(false, false, false, true).orElse(null)))));
        return Optional.of(d);
    }

    private Optional<User> getOptionalUser(){
        User u = new User();
        u.setAppointments(new ArrayList<>(List.of(Objects.requireNonNull(getOptionalAppointment(false, false, true, true).orElse(null)))));
        return Optional.of(u);
    }
}
