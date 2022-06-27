package com.simformsolutions.appointment.service;

import com.simformsolutions.appointment.dto.doctor.DoctorDetailsDto;
import com.simformsolutions.appointment.model.Doctor;
import com.simformsolutions.appointment.model.Speciality;
import com.simformsolutions.appointment.repository.SpecialityRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class DoctorService {


    private final SpecialityRepository specialityRepository;

    private final ModelMapper modelMapper;

    public DoctorService(SpecialityRepository specialityRepository, ModelMapper modelMapper) {
        this.specialityRepository = specialityRepository;
        this.modelMapper = modelMapper;
    }

    //POST
    public DoctorDetailsDto saveDoctor(DoctorDetailsDto doctorDetailsDto) {

        Doctor doctor = modelMapper.map(doctorDetailsDto, Doctor.class);
        Speciality s = specialityRepository.findByTitle(doctor.getSpecialist());
        s.setDoctor(doctor);
        doctorDetailsDto.setDoctorId(specialityRepository.save(s).retrieveLastDoctor().getDoctorId());
        return doctorDetailsDto;
    }
}
