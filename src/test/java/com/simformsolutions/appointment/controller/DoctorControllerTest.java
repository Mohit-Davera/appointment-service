package com.simformsolutions.appointment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.simformsolutions.appointment.dto.doctor.DoctorDetailsDto;
import com.simformsolutions.appointment.service.DoctorService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DoctorControllerTest {

    static final String BASE_URL = "/doctor";
    static final DoctorDetailsDto DOCTOR_DETAILS_DTO = new DoctorDetailsDto(1, "Mohit", "Davera", "9409598787", "davera@gmail.com", "rajkot", "BE", "GEC", 2, "ayurveda",
            LocalTime.parse("10:00", DateTimeFormatter.ofPattern("HH:mm")),
            LocalTime.parse("21:00", DateTimeFormatter.ofPattern("HH:mm")));
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    ObjectWriter objectWriter = objectMapper.writer();
    @MockBean
    private DoctorService doctorService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void registerDoctorSuccess() throws Exception {
        String content = objectWriter.writeValueAsString(DOCTOR_DETAILS_DTO);

        Mockito.when(doctorService.saveDoctor(DOCTOR_DETAILS_DTO)).thenReturn(DOCTOR_DETAILS_DTO);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(content().string(objectWriter.writeValueAsString(DOCTOR_DETAILS_DTO)));
    }
}
