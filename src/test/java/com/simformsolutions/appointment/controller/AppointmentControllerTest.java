package com.simformsolutions.appointment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.simformsolutions.appointment.dto.AppointmentDoctor;
import com.simformsolutions.appointment.dto.appointment.AppointmentDetails;
import com.simformsolutions.appointment.service.AppointmentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.simformsolutions.appointment.constants.AppointmentDetailsConstants.*;
import static com.simformsolutions.appointment.constants.AppointmentDoctorDetailsConstants.*;
import static com.simformsolutions.appointment.constants.DoctorDetailsConstants.*;
import static com.simformsolutions.appointment.constants.SpecialityConstants.SPECIALITY1;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class AppointmentControllerTest {

    static final String BASE_URL = "/appointment";
    static final AppointmentDetails APPOINTMENT_DETAILS_DTO = new AppointmentDetails(SPECIALITY1, ISSUE, APPOINTMENT_DATE, PATIENT_NAME);
    static final AppointmentDoctor APPOINTMENT_DOCTOR_DTO = new AppointmentDoctor(APPOINTMENT_ID1, DOCTOR_ID, DOCTOR_NAME, EXPERIENCE, SPECIALITY1, BOOKING_TIME, BOOKING_DATE, BOOKED_STATUS);
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    ObjectWriter objectWriter = objectMapper.writer();
    @MockBean
    private AppointmentService appointmentService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void bookAppointmentSuccess() throws Exception {
        String content = objectWriter.writeValueAsString(APPOINTMENT_DETAILS_DTO);
        Mockito.when(appointmentService.saveAppointment(APPOINTMENT_DETAILS_DTO, 1)).thenReturn(APPOINTMENT_DOCTOR_DTO);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content).param("userId", String.valueOf(1)))
                .andExpect(status().isOk())
                .andExpect(content().string(objectWriter.writeValueAsString(APPOINTMENT_DOCTOR_DTO)));
    }
}
