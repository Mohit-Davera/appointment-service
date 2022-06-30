package com.simformsolutions.appointment.service;

import com.simformsolutions.appointment.dto.doctor.DoctorDetails;
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
    public DoctorDetails saveDoctor(DoctorDetails doctorDetails) {
        Doctor doctor = modelMapper.map(doctorDetails, Doctor.class);
        Speciality s = specialityRepository.findByTitle(doctor.getSpeciality());
        s.setDoctor(doctor);
        doctorDetails.setDoctorId(specialityRepository.save(s).retrieveLastDoctor().getDoctorId());
        return doctorDetails;
    }
}
