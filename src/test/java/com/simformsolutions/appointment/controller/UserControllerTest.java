package com.simformsolutions.appointment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.simformsolutions.appointment.dto.AppointmentDoctor;
import com.simformsolutions.appointment.dto.user.UserInformation;
import com.simformsolutions.appointment.enums.AppointmentStatus;
import com.simformsolutions.appointment.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.simformsolutions.appointment.constants.AppointmentDoctorDetailsConstants.*;
import static com.simformsolutions.appointment.constants.DoctorDetailsConstants.DOCTOR_ID;
import static com.simformsolutions.appointment.constants.DoctorDetailsConstants.EXPERIENCE;
import static com.simformsolutions.appointment.constants.SpecialityConstants.SPECIALITY1;
import static com.simformsolutions.appointment.constants.UserInformationConstants.*;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    static final String BASE_URL = "/user";

    AppointmentDoctor appointmentDoctorDto1 = new AppointmentDoctor(APPOINTMENT_ID1, DOCTOR_ID, DOCTOR_NAME, EXPERIENCE, SPECIALITY1, BOOKING_TIME, BOOKING_DATE, BOOKED_STATUS);
    AppointmentDoctor appointmentDoctorDto2 = new AppointmentDoctor(APPOINTMENT_ID2, DOCTOR_ID, DOCTOR_NAME, EXPERIENCE, SPECIALITY1, BOOKING_TIME, BOOKING_DATE, BOOKED_STATUS);
    List<AppointmentDoctor> appointmentDoctors = new ArrayList<>(Arrays.asList(appointmentDoctorDto1, appointmentDoctorDto2));
    UserInformation userInformation = new UserInformation(NAME, EMAIL, NUMBER, PASSWORD);
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    ObjectWriter objectWriter = objectMapper.writer();

    private MockMvc mockMvc;

    private final UserService userService = mock(UserService.class);

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new UserController(userService))
                .build();
    }

    @Test
    void registerUserSuccess() throws Exception {
        String content = objectWriter.writeValueAsString(userInformation);
        Mockito.when(userService.addUser(userInformation)).thenReturn(userInformation);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(content().string(objectWriter.writeValueAsString(userInformation)));
    }

    @Test
    void showAppointmentsSuccess() throws Exception {
        Mockito.when(userService.getAppointments(1)).thenReturn(appointmentDoctors);
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/").param("userId", String.valueOf(1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectWriter.writeValueAsString(appointmentDoctors)))
                .andExpect(status().isOk());

    }

    @Test
    void cancelAppointmentSuccess() throws Exception {
        appointmentDoctorDto2.setStatus(AppointmentStatus.CANCELLED.getLabel());
        Mockito.when(userService.cancelAppointment(1)).thenReturn(appointmentDoctorDto2);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/1/cancel").param("userId", String.valueOf(1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectWriter.writeValueAsString(appointmentDoctorDto2)))
                .andExpect(status().isOk());

    }

    @Test
    void rescheduleAppointmentZeroDaysSuccess() throws Exception {
        appointmentDoctorDto1.setStatus(AppointmentStatus.RESCHEDULE.getLabel());
        Mockito.when(userService.rescheduleAppointment(1, 1, "0")).thenReturn(appointmentDoctorDto1);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/1/reschedule")
                        .param("appointmentId", String.valueOf(1))
                        .param("userId", String.valueOf(1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectWriter.writeValueAsString(appointmentDoctorDto1)))
                .andExpect(status().isOk());
    }

    @Test
    void rescheduleAppointmentDaysSuccess() throws Exception {
        appointmentDoctorDto1.setStatus(AppointmentStatus.RESCHEDULE.getLabel());
        appointmentDoctorDto1.setBookedDate(appointmentDoctorDto1.getBookedDate().plusDays(3));
        Mockito.when(userService.rescheduleAppointment(1, 1, "3")).thenReturn(appointmentDoctorDto1);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/1/reschedule")
                        .param("appointmentId", String.valueOf(1))
                        .param("userId", String.valueOf(1))
                        .param("days", "3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectWriter.writeValueAsString(appointmentDoctorDto1)))
                .andExpect(status().isOk());
    }

    @Test
    void availableDoctorsSuccess() throws Exception {
        Mockito.when(userService.getAvailableDoctors(1, 1)).thenReturn(appointmentDoctors);
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/1/doctors")
                        .param("userId", String.valueOf(1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectWriter.writeValueAsString(appointmentDoctors)))
                .andExpect(status().isOk());

    }

    @Test
    void changeDoctorSuccess() throws Exception {
        String content = objectWriter.writeValueAsString(appointmentDoctorDto1);
        Mockito.when(userService.changeDoctor(appointmentDoctorDto1, 1)).thenReturn(appointmentDoctorDto1);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/change-doctor")
                        .param("userId", String.valueOf(1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(content().string(objectWriter.writeValueAsString(appointmentDoctorDto1)))
                .andExpect(status().isOk());
    }

}
