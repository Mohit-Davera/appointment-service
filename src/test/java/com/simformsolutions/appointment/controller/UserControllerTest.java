package com.simformsolutions.appointment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.simformsolutions.appointment.constants.UserInfoConstants;
import com.simformsolutions.appointment.dto.AppointmentDoctor;
import com.simformsolutions.appointment.dto.user.UserInformation;
import com.simformsolutions.appointment.enums.AppointmentStatus;
import com.simformsolutions.appointment.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.simformsolutions.appointment.constants.AppointmentDoctorDetailsConstants.*;
import static com.simformsolutions.appointment.constants.DoctorDetailsConstants.DOCTOR_ID;
import static com.simformsolutions.appointment.constants.DoctorDetailsConstants.EXPERIENCE;
import static com.simformsolutions.appointment.constants.SpecialityConstants.SPECIALITY1;
import static com.simformsolutions.appointment.constants.UserInfoConstants.*;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.PASSWORD;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    static final String BASE_URL = "/user";
    static final AppointmentDoctor APPOINTMENT_DOCTOR_DTO1 = new AppointmentDoctor(APPOINTMENT_ID1, DOCTOR_ID, DOCTOR_NAME, EXPERIENCE, SPECIALITY1, BOOKING_TIME, BOOKING_DATE, BOOKED_STATUS);
    static final AppointmentDoctor APPOINTMENT_DOCTOR_DTO2 = new AppointmentDoctor(APPOINTMENT_ID2, DOCTOR_ID, DOCTOR_NAME, EXPERIENCE, SPECIALITY1, BOOKING_TIME, BOOKING_DATE, BOOKED_STATUS);
    static final List<AppointmentDoctor> APPOINTMENT_DOCTOR_DTOS = new ArrayList<>(Arrays.asList(APPOINTMENT_DOCTOR_DTO1, APPOINTMENT_DOCTOR_DTO2));
    static final UserInformation USER_DETAILS_DTO = new UserInformation(FULL_NAME, EMAIL, NUMBER, PASSWORD);
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    ObjectWriter objectWriter = objectMapper.writer();
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;

    @Test
    void registerUserSuccess() throws Exception {
        String content = objectWriter.writeValueAsString(USER_DETAILS_DTO);
        Mockito.when(userService.addUser(USER_DETAILS_DTO)).thenReturn(USER_DETAILS_DTO);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(content().string(objectWriter.writeValueAsString(USER_DETAILS_DTO)));
    }

    @Test
    void showAppointmentsSuccess() throws Exception {
        Mockito.when(userService.getAppointments(1)).thenReturn(APPOINTMENT_DOCTOR_DTOS);
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/").param("userId", String.valueOf(1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectWriter.writeValueAsString(APPOINTMENT_DOCTOR_DTOS)))
                .andExpect(status().isOk());

    }

    @Test
    void cancelAppointmentSuccess() throws Exception {
        APPOINTMENT_DOCTOR_DTO2.setStatus(AppointmentStatus.CANCELLED.getLabel());
        Mockito.when(userService.cancelAppointment(1)).thenReturn(APPOINTMENT_DOCTOR_DTO2);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/1/cancel").param("userId", String.valueOf(1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectWriter.writeValueAsString(APPOINTMENT_DOCTOR_DTO2)))
                .andExpect(status().isOk());

    }

    @Test
    void rescheduleAppointmentZeroDaysSuccess() throws Exception {
        APPOINTMENT_DOCTOR_DTO1.setStatus(AppointmentStatus.RESCHEDULE.getLabel());
        Mockito.when(userService.rescheduleAppointment(1, 1, "0")).thenReturn(APPOINTMENT_DOCTOR_DTO1);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/1/reschedule")
                        .param("appointmentId", String.valueOf(1))
                        .param("userId", String.valueOf(1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectWriter.writeValueAsString(APPOINTMENT_DOCTOR_DTO1)))
                .andExpect(status().isOk());
    }

    @Test
    void rescheduleAppointmentDaysSuccess() throws Exception {
        APPOINTMENT_DOCTOR_DTO1.setStatus(AppointmentStatus.RESCHEDULE.getLabel());
        APPOINTMENT_DOCTOR_DTO1.setBookedDate(APPOINTMENT_DOCTOR_DTO1.getBookedDate().plusDays(3));
        Mockito.when(userService.rescheduleAppointment(1, 1, "3")).thenReturn(APPOINTMENT_DOCTOR_DTO1);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/1/reschedule")
                        .param("appointmentId", String.valueOf(1))
                        .param("userId", String.valueOf(1))
                        .param("days", "3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectWriter.writeValueAsString(APPOINTMENT_DOCTOR_DTO1)))
                .andExpect(status().isOk());
    }

    @Test
    void availableDoctorsSuccess() throws Exception {
        Mockito.when(userService.getAvailableDoctors(1, 1)).thenReturn(APPOINTMENT_DOCTOR_DTOS);
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/1/doctors")
                        .param("userId", String.valueOf(1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectWriter.writeValueAsString(APPOINTMENT_DOCTOR_DTOS)))
                .andExpect(status().isOk());

    }

    @Test
    void changeDoctorSuccess() throws Exception {
        String content = objectWriter.writeValueAsString(APPOINTMENT_DOCTOR_DTO1);
        Mockito.when(userService.changeDoctor(APPOINTMENT_DOCTOR_DTO1, 1)).thenReturn(APPOINTMENT_DOCTOR_DTO1);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/change-doctor")
                        .param("userId", String.valueOf(1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(content().string(objectWriter.writeValueAsString(APPOINTMENT_DOCTOR_DTO1)))
                .andExpect(status().isOk());
    }

}
