package com.simformsolutions.appointment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.simformsolutions.appointment.dto.speciality.SpecialityTitleDto;
import com.simformsolutions.appointment.service.SpecialityService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class SpecialityControllerTest {

    static final String BASE_URL = "/speciality";
    static final SpecialityTitleDto SPECIALITY_TITLE_DTO = new SpecialityTitleDto(Arrays.asList("ayurveda", "dental surgeon", "orthopedist"));
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    ObjectWriter objectWriter = objectMapper.writer();
    @MockBean
    private SpecialityService specialityService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void registerDoctorSuccess() throws Exception {
        Mockito.when(specialityService.showSpecialities()).thenReturn(SPECIALITY_TITLE_DTO);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(objectWriter.writeValueAsString(SPECIALITY_TITLE_DTO)));
    }

    @Test
    void addSpecialitiesSuccess() throws Exception {
        Mockito.when(specialityService.saveNewSpecialities(SPECIALITY_TITLE_DTO.getTitles())).thenReturn(SPECIALITY_TITLE_DTO);
        String content = objectWriter.writeValueAsString(SPECIALITY_TITLE_DTO);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(content().string(objectWriter.writeValueAsString(SPECIALITY_TITLE_DTO)));
    }
}
