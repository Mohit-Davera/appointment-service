package com.simformsolutions.appointment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.simformsolutions.appointment.dto.speciality.SpecialityTitle;
import com.simformsolutions.appointment.service.SpecialityService;
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

import java.util.Arrays;

import static com.simformsolutions.appointment.constants.SpecialityConstants.*;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class SpecialityControllerTest {

    static final String BASE_URL = "/speciality";
    SpecialityTitle specialityTitle = new SpecialityTitle(Arrays.asList(SPECIALITY1, SPECIALITY2, SPECIALITY3));
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    ObjectWriter objectWriter = objectMapper.writer();

    private MockMvc mockMvc;
    private final SpecialityService specialityService = mock(SpecialityService.class);

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new SpecialityController(specialityService))
                .build();
    }

    @Test
    void registerDoctorSuccess() throws Exception {
        Mockito.when(specialityService.showSpecialities()).thenReturn(specialityTitle);
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(objectWriter.writeValueAsString(specialityTitle)));
    }

    @Test
    void addSpecialitiesSuccess() throws Exception {
        Mockito.when(specialityService.saveNewSpecialities(specialityTitle.getTitles())).thenReturn(specialityTitle);
        String content = objectWriter.writeValueAsString(specialityTitle);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(content().string(objectWriter.writeValueAsString(specialityTitle)));
    }
}
