package com.simformsolutions.appointment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.simformsolutions.appointment.dto.AppointmentDoctor;
import com.simformsolutions.appointment.dto.appointment.AppointmentDetails;
import com.simformsolutions.appointment.service.AppointmentService;
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

import static com.simformsolutions.appointment.constants.AppointmentDetailsConstants.*;
import static com.simformsolutions.appointment.constants.AppointmentDoctorDetailsConstants.*;
import static com.simformsolutions.appointment.constants.DoctorDetailsConstants.DOCTOR_ID;
import static com.simformsolutions.appointment.constants.DoctorDetailsConstants.EXPERIENCE;
import static com.simformsolutions.appointment.constants.SpecialityConstants.SPECIALITY1;
import static com.simformsolutions.appointment.constants.UserInformationConstants.USER_ID;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class AppointmentControllerTest {

    static final String BASE_URL = "/appointment";

    AppointmentDetails appointmentDetails = new AppointmentDetails(SPECIALITY1, ISSUE, APPOINTMENT_DATE, PATIENT_NAME);
    AppointmentDoctor appointmentDoctor = new AppointmentDoctor(APPOINTMENT_ID1, DOCTOR_ID, DOCTOR_NAME, EXPERIENCE, SPECIALITY1, BOOKING_TIME, BOOKING_DATE, BOOKED_STATUS);
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    ObjectWriter objectWriter = objectMapper.writer();
    private MockMvc mockMvc;
    private final AppointmentService appointmentService = mock(AppointmentService.class);

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new AppointmentController(appointmentService))
                .build();
    }

    @Test
    void bookAppointmentSuccess() throws Exception {
        String content = objectWriter.writeValueAsString(appointmentDetails);
        Mockito.when(appointmentService.saveAppointment(appointmentDetails, USER_ID)).thenReturn(appointmentDoctor);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content).param("userId", String.valueOf(1)))
                .andExpect(status().isOk());
    }
}
