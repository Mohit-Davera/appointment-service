package com.simformsolutions.appointment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.simformsolutions.appointment.dto.doctor.DoctorDetails;
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

import static com.simformsolutions.appointment.constants.DoctorDetailsConstants.*;
import static com.simformsolutions.appointment.constants.SpecialityConstants.SPECIALITY1;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class DoctorControllerTest {

    static final String BASE_URL = "/doctor";
    static final DoctorDetails DOCTOR_DETAILS_DTO = new DoctorDetails(DOCTOR_ID, FIRST_NAME, LAST_NAME, PHONE_NUMBER, EMAIL, CITY, DEGREE, COLLEGE_NAME, EXPERIENCE, SPECIALITY1,
            ENTRY_TIME,
            EXIT_TIME);
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
