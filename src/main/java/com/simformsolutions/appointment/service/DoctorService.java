package com.simformsolutions.appointment.service;

import com.simformsolutions.appointment.dto.doctor.DoctorDetails;
import com.simformsolutions.appointment.dto.doctor.DoctorUpdateDetails;
import com.simformsolutions.appointment.excepetion.DoctorNotFoundException;
import com.simformsolutions.appointment.model.Doctor;
import com.simformsolutions.appointment.model.Role;
import com.simformsolutions.appointment.model.RoleDetails;
import com.simformsolutions.appointment.model.Speciality;
import com.simformsolutions.appointment.repository.DoctorRepository;
import com.simformsolutions.appointment.repository.RoleRepository;
import com.simformsolutions.appointment.repository.SpecialityRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DoctorService {

    private final SpecialityRepository specialityRepository;
    private final ModelMapper modelMapper;


    private final DoctorRepository doctorRepository;

    private final RoleRepository roleRepository;

    public DoctorService(SpecialityRepository specialityRepository, ModelMapper modelMapper, DoctorRepository doctorRepository, RoleRepository roleRepository) {
        this.specialityRepository = specialityRepository;
        this.modelMapper = modelMapper;
        this.doctorRepository = doctorRepository;
        this.roleRepository = roleRepository;
    }

    //POST
    public DoctorDetails saveDoctor(DoctorDetails doctorDetails) {
        Doctor doctor = modelMapper.map(doctorDetails, Doctor.class);
        Speciality s = specialityRepository.findByTitle(doctor.getSpeciality());
        Optional<Role> optionalRole = roleRepository.findById(2);
        if (optionalRole.isPresent()) {
            optionalRole.get().addRoleDetail(new RoleDetails(doctorDetails.getEmail()));
            roleRepository.save(optionalRole.get());
        }
        doctor.setEnabled(true);
        s.setDoctor(doctor);
        doctorDetails.setDoctorId(specialityRepository.save(s).retrieveLastDoctor().getDoctorId());
        return doctorDetails;
    }

}
