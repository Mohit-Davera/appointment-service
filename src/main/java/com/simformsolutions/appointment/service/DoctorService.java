package com.simformsolutions.appointment.service;

import com.simformsolutions.appointment.dto.doctor.DoctorDetailsDto;
import com.simformsolutions.appointment.model.Doctor;
import com.simformsolutions.appointment.model.Speciality;
import com.simformsolutions.appointment.repository.DoctorRepository;
import com.simformsolutions.appointment.repository.SpecialityRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorService {


    private final DoctorRepository doctorRepository;

    private final SpecialityRepository specialityRepository;

    private final ModelMapper modelMapper;

    public DoctorService(DoctorRepository doctorRepository, SpecialityRepository specialityRepository,ModelMapper modelMapper) {
        this.doctorRepository = doctorRepository;
        this.specialityRepository = specialityRepository;
        this.modelMapper = modelMapper;
    }

    //GET
    public List<Doctor> getAllDoctors(){
        return doctorRepository.findAll();
    }
    public Doctor getDoctor(int id){
        return doctorRepository.findById(id).orElseThrow();
    }

    //POST
    public DoctorDetailsDto saveDoctor(DoctorDetailsDto doctorDetailsDto){

        Doctor doctor = modelMapper.map(doctorDetailsDto,Doctor.class);
        Speciality s = specialityRepository.findByTitle(doctor.getSpecialist());
        s.setDoctor(doctor);
        doctorDetailsDto.setDoctorId(specialityRepository.save(s).retrieveLastDoctor().getDoctorId());
        return doctorDetailsDto;
    }
}
