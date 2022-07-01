package com.simformsolutions.appointment.service;

import com.simformsolutions.appointment.converter.AppointmentDoctorDtoConverter;
import com.simformsolutions.appointment.dto.AppointmentDoctor;
import com.simformsolutions.appointment.dto.appointment.AppointmentDetails;
import com.simformsolutions.appointment.dto.user.UserInformation;
import com.simformsolutions.appointment.excepetion.AppointmentNotFoundException;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import javax.persistence.Tuple;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static com.simformsolutions.appointment.constants.AppointmentDetailsConstants.*;
import static com.simformsolutions.appointment.constants.AppointmentDoctorDetailsConstants.*;
import static com.simformsolutions.appointment.constants.DoctorDetailsConstants.DOCTOR_ID;
import static com.simformsolutions.appointment.constants.DoctorDetailsConstants.EXPERIENCE;
import static com.simformsolutions.appointment.constants.SpecialityConstants.SPECIALITY1;
import static com.simformsolutions.appointment.constants.UserInformationConstants.*;
import static com.simformsolutions.appointment.enums.AppointmentStatus.AVAILABLE;
import static com.simformsolutions.appointment.enums.AppointmentStatus.CANCELLED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final ModelMapper modelMapper = mock(ModelMapper.class);
    private final AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
    private final DoctorRepository doctorRepository = mock(DoctorRepository.class);
    private final ScheduleRepository scheduleRepository = mock(ScheduleRepository.class);
    private final AppointmentDoctorDtoConverter appointmentDoctorDtoConverter = mock(AppointmentDoctorDtoConverter.class);
    private final AppointmentService appointmentService = mock(AppointmentService.class);
    UserInformation userInformation = new UserInformation(NAME, EMAIL, NUMBER, PASSWORD);
    AppointmentDoctor appointmentDoctor1 = new AppointmentDoctor(APPOINTMENT_ID1, DOCTOR_ID, DOCTOR_NAME, EXPERIENCE, SPECIALITY1, BOOKING_TIME, BOOKING_DATE, BOOKED_STATUS);
    AppointmentDetails appointmentDetails = new AppointmentDetails(SPECIALITY1, ISSUE, APPOINTMENT_DATE, PATIENT_NAME);
    AppointmentDoctor appointmentDoctor = new AppointmentDoctor(APPOINTMENT_ID2, DOCTOR_ID, DOCTOR_NAME, EXPERIENCE, SPECIALITY1, BOOKING_TIME, BOOKING_DATE, BOOKED_STATUS);
    List<AppointmentDoctor> listOfAppointmentDoctor = new ArrayList<>(Arrays.asList(appointmentDoctor1, appointmentDoctor));
    UserService userService = new UserService(userRepository, doctorRepository, appointmentService, appointmentDoctorDtoConverter, appointmentRepository, scheduleRepository, modelMapper);

    @Test
    void addUserSuccess() {
        //Expected
        Mockito.when(modelMapper.map(any(UserInformation.class), any())).thenReturn(getUserDetails(false));
        Mockito.when(userRepository.save(any(User.class))).thenReturn(getUserDetails(true));
        //Actual Data
        UserInformation userInformation1 = userService.addUser(getUserDetailsDto(false));
        assertEquals(getUserDetailsDto(true).getUserId(), userInformation1.getUserId());
    }

    @Test
    void addUserConflict() {
        //Expected
        Mockito.when(modelMapper.map(any(UserInformation.class), any())).thenReturn(getUserDetails(false));
        Mockito.when(userRepository.save(any(User.class))).thenThrow(DataIntegrityViolationException.class);
        //Actual Data
        UserInformation userInformation = getUserDetailsDto(false);
        assertThrows(DataIntegrityViolationException.class, () -> userService.addUser(userInformation));
    }

    @Test
    void getAppointmentsSuccess() {
        Mockito.when(appointmentRepository.findDetailsOfAppointments(USER_ID)).thenReturn(getListTuple());
        Mockito.when(appointmentDoctorDtoConverter.tuplesToAppointmentDoctorConverter(Mockito.anyList())).thenReturn(listOfAppointmentDoctor);
        System.out.println(listOfAppointmentDoctor);
        assertEquals(listOfAppointmentDoctor, userService.getAppointments(USER_ID));
    }

    @Test
    void rescheduleAppointmentsAppointmentFailure() {
        Mockito.when(appointmentRepository.findById(APPOINTMENT_ID1)).thenReturn(getOptionalAppointment(true, false, false, false));
        assertThrows(AppointmentNotFoundException.class, () -> userService.rescheduleAppointment(APPOINTMENT_ID1, USER_ID, "2"));
    }

    @Test
    void rescheduleAppointmentsCancelledStatusFailure() {
        Mockito.when(appointmentRepository.findById(APPOINTMENT_ID1)).thenReturn(getOptionalAppointment(false, true, true, false));
        assertThrows(StatusChangeException.class, () -> userService.rescheduleAppointment(APPOINTMENT_ID1, USER_ID, "2"));
    }

    @Test
    void rescheduleAppointmentsBeforeStatusFailure() {
        Mockito.when(appointmentRepository.findById(APPOINTMENT_ID1)).thenReturn(getOptionalAppointment(false, false, false, false));
        assertThrows(StatusChangeException.class, () -> userService.rescheduleAppointment(APPOINTMENT_ID1, USER_ID, "2"));
    }

    @Test
    void rescheduleAppointmentsWithDaysSuccess() {
        Mockito.when(appointmentRepository.findById(APPOINTMENT_ID1)).thenReturn(getOptionalAppointment(false, false, true, false));
        Mockito.when(modelMapper.map(any(UserInformation.class), any())).thenReturn(appointmentDetails);
        Mockito.when(appointmentService.saveAppointment(appointmentDetails, USER_ID)).thenReturn(appointmentDoctor1);
        Mockito.when(appointmentRepository.save(Objects.requireNonNull(getOptionalAppointment(false, false, true, false).orElse(null)))).thenReturn(getOptionalAppointment(false, false, true, true).orElse(null));
        Mockito.when(appointmentRepository.findDetailsOfAppointment(APPOINTMENT_ID1)).thenReturn(getListTuple());
        Mockito.when(appointmentDoctorDtoConverter.tuplesToAppointmentDoctorConverter(anyList())).thenReturn(listOfAppointmentDoctor);
        assertEquals(listOfAppointmentDoctor.get(0), userService.rescheduleAppointment(APPOINTMENT_ID1, USER_ID, "2"));
    }

    @Test
    void cancelAppointmentFailure() {
        Mockito.when(appointmentRepository.findById(APPOINTMENT_ID1)).thenReturn(getOptionalAppointment(true, false, true, false));
        assertThrows(AppointmentNotFoundException.class, () -> userService.cancelAppointment(APPOINTMENT_ID1));
    }

    @Test
    void cancelAppointmentSuccess() {
        Optional<Appointment> optionalAppointment = getOptionalAppointment(false, false, true, false);
        Mockito.when(appointmentRepository.findById(APPOINTMENT_ID1)).thenReturn(optionalAppointment);
        assert optionalAppointment.orElse(null) != null;
        Mockito.when(appointmentRepository.save(optionalAppointment.orElse(null))).thenReturn(optionalAppointment.orElse(null));
        Mockito.when(appointmentRepository.findDetailsOfAppointment(APPOINTMENT_ID1)).thenReturn(getListTuple());
        Mockito.when(appointmentDoctorDtoConverter.tuplesToAppointmentDoctorConverter(anyList())).thenReturn(listOfAppointmentDoctor);
        assertEquals(listOfAppointmentDoctor.get(0), userService.cancelAppointment(APPOINTMENT_ID1));

    }

    @Test
    void getAvailableDoctorsFailure() {
        Mockito.when(appointmentRepository.findById(APPOINTMENT_ID1)).thenReturn(getOptionalAppointment(true, false, true, false));
        assertThrows(AppointmentNotFoundException.class, () -> userService.getAvailableDoctors(APPOINTMENT_ID1, USER_ID));
    }

    @Test
    void getAvailableDoctorsSuccess() {
        Optional<Appointment> optionalAppointment = getOptionalAppointment(false, false, true, false);
        Mockito.when(appointmentRepository.findById(APPOINTMENT_ID1)).thenReturn(optionalAppointment);
        assert optionalAppointment.orElse(null) != null;
        Mockito.when(appointmentService.bookAppointmentAgain(optionalAppointment.orElse(null), USER_ID)).thenReturn(listOfAppointmentDoctor);
        assertEquals(listOfAppointmentDoctor, userService.getAvailableDoctors(APPOINTMENT_ID1, USER_ID));
    }

    @Test
    void changeDoctorFailure() {
        Mockito.when(scheduleRepository.getScheduleFromAppointmentId(appointmentDoctor1.getAppointmentId())).thenReturn(getOptionalSchedule(true, false));
        assertThrows(ScheduleNotFoundException.class, () -> userService.changeDoctor(appointmentDoctor1, USER_ID));
    }

    @Test
    void changeDoctorSuccess() {
        Optional<Schedule> optionalSchedule = Optional.of(new Schedule(LocalTime.now(), LocalDate.now(), getOptionalDoctor().orElse(null), getOptionalUser().orElse(null), new Appointment(APPOINTMENT_ID1)));
        optionalSchedule.orElse(null).setId(1);
        optionalSchedule.orElse(null).setAppointment(new Appointment(APPOINTMENT_ID1));
        Schedule schedule = optionalSchedule.orElse(null);
        Mockito.when(scheduleRepository.getScheduleFromAppointmentId(appointmentDoctor1.getAppointmentId())).thenReturn(getOptionalSchedule(false, true));
        Mockito.when(userRepository.findById(USER_ID)).thenReturn(getOptionalUser());
        Mockito.when(doctorRepository.findById(DOCTOR_ID)).thenReturn(getOptionalDoctor());
        Mockito.when(appointmentRepository.findById(APPOINTMENT_ID1)).thenReturn(Optional.of(new Appointment(APPOINTMENT_ID1)));
        Mockito.when(scheduleRepository.save(any(Schedule.class))).thenReturn(schedule);
        assertEquals(appointmentDoctor1, userService.changeDoctor(appointmentDoctor1, USER_ID));
    }

    private UserInformation getUserDetailsDto(boolean hasId) {
        if (hasId) {
            userInformation.setUserId(USER_ID);
            return userInformation;
        }
        return userInformation;
    }

    private User getUserDetails(boolean hasId) {
        if (hasId)
            return new User(USER_ID, NAME, EMAIL, NUMBER, PASSWORD);
        return new User(NAME, EMAIL, NUMBER, PASSWORD);
    }

    private List<Tuple> getListTuple() {
        Tuple mockedTuple1 = mock(Tuple.class);
        Tuple mockedTuple2 = mock(Tuple.class);
        List<Tuple> tuples = new ArrayList<>();
        tuples.add(0, mockedTuple1);
        tuples.add(1, mockedTuple2);
        return tuples;
    }

    private Optional<Appointment> getOptionalAppointment(boolean isEmpty, boolean isCancelled, boolean isAfterCurrentTime, boolean hasId) {
        if (isEmpty) {
            return Optional.empty();
        }
        Optional<Appointment> optionalAppointment = Optional.of(new Appointment(APPOINTMENT_ID1, SPECIALITY1, BOOKING_TIME
                , BOOKING_DATE, PATIENT_NAME, ISSUE, AVAILABLE));
        if (hasId)
            optionalAppointment.orElse(null).setAppointmentId(APPOINTMENT_ID1);
        if (isAfterCurrentTime) {
            if (isCancelled) {
                optionalAppointment.orElse(null).setStatus(CANCELLED);
                optionalAppointment.orElse(null).setEndTime(LocalTime.now().plusHours(1));
            } else
                optionalAppointment.orElse(null).setEndTime(LocalTime.now().plusHours(1));
        }
        if (isCancelled)
            optionalAppointment.orElse(null).setStatus(CANCELLED);
        return optionalAppointment;
    }

    private Optional<Schedule> getOptionalSchedule(boolean isEmpty, boolean hasId) {
        if (isEmpty) {
            return Optional.empty();
        }
        Optional<Schedule> optionalSchedule = Optional.of(new Schedule(LocalTime.now(), LocalDate.now(), getOptionalDoctor().orElse(null), getOptionalUser().orElse(null), getOptionalAppointment(false, false, true, true).orElse(null)));
        if (hasId)
            optionalSchedule.orElse(null).setId(1);
        optionalSchedule.orElse(null).setAppointment(new Appointment(APPOINTMENT_ID1));
        return optionalSchedule;
    }

    private Optional<Doctor> getOptionalDoctor() {
        Doctor d = new Doctor();
        d.setAppointments(new ArrayList<>(List.of(Objects.requireNonNull(getOptionalAppointment(false, false, false, true).orElse(null)))));
        return Optional.of(d);
    }

    private Optional<User> getOptionalUser() {
        User u = new User();
        u.setAppointments(new ArrayList<>(List.of(Objects.requireNonNull(getOptionalAppointment(false, false, true, true).orElse(null)))));
        return Optional.of(u);
    }
}
