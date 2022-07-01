package com.simformsolutions.appointment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.simformsolutions.appointment.dto.doctor.DoctorDetails;
import com.simformsolutions.appointment.service.DoctorService;
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

import static com.simformsolutions.appointment.constants.DoctorDetailsConstants.*;
import static com.simformsolutions.appointment.constants.SpecialityConstants.SPECIALITY1;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class DoctorControllerTest {

    static final String BASE_URL = "/doctor";
    DoctorDetails doctorDetails = new DoctorDetails(DOCTOR_ID, FIRST_NAME, LAST_NAME, PHONE_NUMBER, EMAIL, CITY, DEGREE, COLLEGE_NAME, EXPERIENCE, SPECIALITY1,
            ENTRY_TIME,
            EXIT_TIME);
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    ObjectWriter objectWriter = objectMapper.writer();
    private final DoctorService doctorService = mock(DoctorService.class);
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new DoctorController(doctorService))
                .build();
    }

    @Test
    void registerDoctorSuccess() throws Exception {
        String content = objectWriter.writeValueAsString(doctorDetails);
        Mockito.when(doctorService.saveDoctor(doctorDetails)).thenReturn(doctorDetails);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(content().string(objectWriter.writeValueAsString(doctorDetails)));
    }
}
