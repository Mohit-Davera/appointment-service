package com.simformsolutions.appointment.service;

import com.simformsolutions.appointment.dto.speciality.SpecialityTitleDto;
import com.simformsolutions.appointment.model.Doctor;
import com.simformsolutions.appointment.model.Speciality;
import com.simformsolutions.appointment.repository.SpecialityRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
class SpecialityServiceTest {

    static final Doctor DOCTOR_WITHOUT_ID = new Doctor(0, "Mohit", "D", "9409598787", "davera@gmail.com", "rajkot", "BE", "GEC", 2, "ayurveda",
            LocalTime.parse("10:00", DateTimeFormatter.ofPattern("HH:mm")),
            LocalTime.parse("21:00", DateTimeFormatter.ofPattern("HH:mm")), null);

    static final SpecialityTitleDto SPECIALITY_TITLE_DTO = new SpecialityTitleDto(new ArrayList<>(Arrays.asList("ayurveda", "dental surgeon", "cardiologist")));

    @Autowired
    SpecialityService specialityService;

    @MockBean
    SpecialityRepository specialityRepository;

    @Test
    void showSpecialitiesSuccess() {
        Mockito.when(specialityRepository.findAll()).thenReturn(getSpecialities(true));
        assertEquals(SPECIALITY_TITLE_DTO.getTitles(), specialityService.showSpecialities().getTitles());
    }

    @Test
    void saveNewSpecialitiesSuccess() {
        Mockito.when(specialityRepository.saveAll(getSpecialities(false))).thenReturn(getSpecialities(true));
        assertEquals(getSpecialities(true).stream().map(Speciality::getTitle).collect(Collectors.toList()), specialityService.saveNewSpecialities(Arrays.asList("ayurveda", "dental surgeon", "cardiologist")).getTitles());

    }

    List<Speciality> getSpecialities(boolean withId) {


        Speciality speciality1 = new Speciality(0, "ayurveda", new ArrayList<>(List.of(DOCTOR_WITHOUT_ID)));
        Speciality speciality2 = new Speciality(0, "dental surgeon", new ArrayList<>(List.of(DOCTOR_WITHOUT_ID)));
        Speciality speciality3 = new Speciality(0, "cardiologist", new ArrayList<>(List.of(DOCTOR_WITHOUT_ID)));
        if (withId) {
            speciality1.setSpecialityId(1);
            speciality2.setSpecialityId(2);
            speciality3.setSpecialityId(3);
        }
        return new ArrayList<>(Arrays.asList(speciality1, speciality2, speciality3));
    }
}
