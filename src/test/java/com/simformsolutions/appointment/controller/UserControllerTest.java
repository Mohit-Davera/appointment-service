package com.simformsolutions.appointment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.simformsolutions.appointment.dto.AppointmentDoctorDto;
import com.simformsolutions.appointment.dto.user.UserDetailsDto;
import com.simformsolutions.appointment.enums.AppointmentStatus;
import com.simformsolutions.appointment.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    static final String BASE_URL = "/user";
    static final AppointmentDoctorDto APPOINTMENT_DOCTOR_DTO1 = new AppointmentDoctorDto(1, 1, "Ravi D", 1, "ayurveda", LocalTime.parse("10:00", DateTimeFormatter.ofPattern("HH:mm")), LocalDate.parse("17/12/2022", DateTimeFormatter.ofPattern("dd/MM/yyyy")), "BOOKED");
    static final AppointmentDoctorDto APPOINTMENT_DOCTOR_DTO2 = new AppointmentDoctorDto(2, 1, "Ravi D", 1, "ayurveda", LocalTime.parse("11:00", DateTimeFormatter.ofPattern("HH:mm")), LocalDate.parse("18/12/2022", DateTimeFormatter.ofPattern("dd/MM/yyyy")), "BOOKED");
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    ObjectWriter objectWriter = objectMapper.writer();
    static final UserDetailsDto USER_DETAILS_DTO = new UserDetailsDto("Monit D", "mohit@gmail.com", "0123456789", "password");
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;

    @Test
    public void registerUserSuccess() throws Exception {
        UserDetailsDto USER_DETAILS_DTO = new UserDetailsDto("Mohit D", "mohit@gmail.com", "0123456789", "password");
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
    void registerUserConflict() throws Exception {

        String content = objectWriter.writeValueAsString(USER_DETAILS_DTO);
        Mockito.when(userService.addUser(USER_DETAILS_DTO)).thenThrow(new DataIntegrityViolationException("Already Exists", new javax.persistence.EntityExistsException()));

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isConflict());
    }

    @Test
    void getAppointmentsSuccess() throws Exception {
        List<AppointmentDoctorDto> appointmentDoctors = new ArrayList<>(Arrays.asList(APPOINTMENT_DOCTOR_DTO1, APPOINTMENT_DOCTOR_DTO2));
        String content = objectWriter.writeValueAsString(appointmentDoctors);

        Mockito.when(userService.getAppointments(1)).thenReturn(appointmentDoctors);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/").param("userId", String.valueOf(1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectWriter.writeValueAsString(appointmentDoctors)))
                .andExpect(status().isOk());

    }

    @Test
    void cancelAppointmentSuccess() throws Exception {

        APPOINTMENT_DOCTOR_DTO2.setStatus(AppointmentStatus.CANCELLED.getLabel());
        String content = objectWriter.writeValueAsString(APPOINTMENT_DOCTOR_DTO2);

        Mockito.when(userService.cancelAppointment(1)).thenReturn(APPOINTMENT_DOCTOR_DTO2);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/1/cancel").param("userId", String.valueOf(1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectWriter.writeValueAsString(APPOINTMENT_DOCTOR_DTO2)))
                .andExpect(status().isOk());


    }

    @Test
    void rescheduleAppointmentSuccess() throws Exception {

        APPOINTMENT_DOCTOR_DTO1.setStatus(AppointmentStatus.RESCHEDULE.getLabel());
        String content = objectWriter.writeValueAsString(APPOINTMENT_DOCTOR_DTO1);

        Mockito.when(userService.rescheduleAppointment(1, 1, "0")).thenReturn(APPOINTMENT_DOCTOR_DTO1);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/1/reschedule")
                        .param("appointmentId", String.valueOf(1))
                        .param("userId", String.valueOf(1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectWriter.writeValueAsString(APPOINTMENT_DOCTOR_DTO1)))
                .andExpect(status().isOk());
    }

    @Test
    void availableDoctorsSuccess() throws Exception {
        List<AppointmentDoctorDto> appointmentDoctors = new ArrayList<>(Arrays.asList(APPOINTMENT_DOCTOR_DTO1, APPOINTMENT_DOCTOR_DTO2));
        String content = objectWriter.writeValueAsString(appointmentDoctors);

        Mockito.when(userService.getAvailableDoctors(1, 1)).thenReturn(appointmentDoctors);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/1/doctors")
                        .param("userId", String.valueOf(1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectWriter.writeValueAsString(appointmentDoctors)))
                .andExpect(status().isOk());

    }

    @Test
    void changeDoctorSuccess() throws Exception {

        String content = objectWriter.writeValueAsString(APPOINTMENT_DOCTOR_DTO1);

        Mockito.when(userService.changeDoctor(APPOINTMENT_DOCTOR_DTO1, 1)).thenReturn(APPOINTMENT_DOCTOR_DTO1);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/changedoctor")
                        .param("userId", String.valueOf(1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(content().string(objectWriter.writeValueAsString(APPOINTMENT_DOCTOR_DTO1)))
                .andExpect(status().isOk());
    }

}
