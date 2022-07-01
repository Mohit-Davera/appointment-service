package com.simformsolutions.appointment.service;

import com.simformsolutions.appointment.dto.speciality.SpecialityTitle;
import com.simformsolutions.appointment.model.Doctor;
import com.simformsolutions.appointment.model.Speciality;
import com.simformsolutions.appointment.repository.SpecialityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.simformsolutions.appointment.constants.DoctorDetailsConstants.*;
import static com.simformsolutions.appointment.constants.SpecialityConstants.*;
import static com.simformsolutions.appointment.constants.UserInformationConstants.ZERO_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class SpecialityServiceTest {

    Doctor doctorWithoutId = new Doctor(ZERO_ID, FIRST_NAME, LAST_NAME, PHONE_NUMBER, EMAIL, CITY, DEGREE, COLLEGE_NAME, EXPERIENCE, SPECIALITY1,
            ENTRY_TIME,
            EXIT_TIME, null);

    SpecialityTitle specialityTitle = new SpecialityTitle(new ArrayList<>(Arrays.asList(SPECIALITY1, SPECIALITY2, SPECIALITY3)));

    private final SpecialityRepository specialityRepository = mock(SpecialityRepository.class);
    private final SpecialityService specialityService = new SpecialityService(specialityRepository);

    @Test
    void showSpecialitiesSuccess() {
        Mockito.when(specialityRepository.findAll()).thenReturn(getSpecialities(true));
        assertEquals(specialityTitle.getTitles(), specialityService.showSpecialities().getTitles());
    }

    @Test
    void saveNewSpecialitiesSuccess() {
        Mockito.when(specialityRepository.saveAll(getSpecialities(false))).thenReturn(getSpecialities(true));
        assertEquals(getSpecialities(true).stream().map(Speciality::getTitle).collect(Collectors.toList()), specialityService.saveNewSpecialities(Arrays.asList(SPECIALITY1, SPECIALITY2, SPECIALITY3)).getTitles());

    }

    List<Speciality> getSpecialities(boolean hasId) {
        Speciality speciality1 = new Speciality(SPECIALITY1, new ArrayList<>(List.of(doctorWithoutId)));
        Speciality speciality2 = new Speciality(SPECIALITY2, new ArrayList<>(List.of(doctorWithoutId)));
        Speciality speciality3 = new Speciality(SPECIALITY3, new ArrayList<>(List.of(doctorWithoutId)));
        if (hasId) {
            speciality1.setSpecialityId(SPECIALITY_ID1);
            speciality2.setSpecialityId(SPECIALITY_ID2);
            speciality3.setSpecialityId(SPECIALITY_ID3);
        }
        return new ArrayList<>(Arrays.asList(speciality1, speciality2, speciality3));
    }
}
