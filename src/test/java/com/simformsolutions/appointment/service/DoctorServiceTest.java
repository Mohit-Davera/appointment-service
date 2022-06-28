package com.simformsolutions.appointment.service;

import com.simformsolutions.appointment.dto.doctor.DoctorDetailsDto;
import com.simformsolutions.appointment.model.Doctor;
import com.simformsolutions.appointment.model.Speciality;
import com.simformsolutions.appointment.repository.SpecialityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {



    static final DoctorDetailsDto DOCTOR_DETAILS_DTO = new DoctorDetailsDto(1, "Mohit", "Davera", "9409598787", "davera@gmail.com", "rajkot", "BE", "GEC", 2, "ayurveda",
            LocalTime.parse("10:00", DateTimeFormatter.ofPattern("HH:mm")),
            LocalTime.parse("21:00", DateTimeFormatter.ofPattern("HH:mm")));


    static final Doctor DOCTOR_WITH_ID = new Doctor(1, "Mohit", "Davera", "9409598787", "davera@gmail.com", "rajkot", "BE", "GEC", 2, "ayurveda",
            LocalTime.parse("10:00", DateTimeFormatter.ofPattern("HH:mm")),
            LocalTime.parse("21:00", DateTimeFormatter.ofPattern("HH:mm")), null);

    static final Doctor DOCTOR_WITHOUT_ID = new Doctor(0, "Mohit", "D", "9409598787", "davera@gmail.com", "rajkot", "BE", "GEC", 2, "ayurveda",
            LocalTime.parse("10:00", DateTimeFormatter.ofPattern("HH:mm")),
            LocalTime.parse("21:00", DateTimeFormatter.ofPattern("HH:mm")), null);


    private final SpecialityRepository specialityRepository = mock(SpecialityRepository.class);

    private final ModelMapper modelMapper = mock(ModelMapper.class);
    private final DoctorService doctorService = new DoctorService(specialityRepository,modelMapper);


    @Test
    void saveDoctorSuccess() {
        Mockito.when(modelMapper.map(any(DoctorDetailsDto.class), any())).thenReturn(DOCTOR_WITH_ID);
        Mockito.when(specialityRepository.findByTitle(DOCTOR_DETAILS_DTO.getSpecialist())).thenReturn(getSpeciality(false));
        Mockito.when(specialityRepository.save(any(Speciality.class))).thenReturn(getSpeciality(true));
        assertEquals(getSpeciality(true).getDoctors().get(0).getDoctorId(), doctorService.saveDoctor(DOCTOR_DETAILS_DTO).getDoctorId());
    }

    Speciality getSpeciality(boolean withId) {

        Speciality speciality = new Speciality(1, "ayurveda", new ArrayList<>(List.of(DOCTOR_WITHOUT_ID)));
        if (withId)
            speciality.setSpecialityId(1);
        return speciality;
    }
}
