package com.simformsolutions.doctorappointmentsystem.service;

import com.simformsolutions.doctorappointmentsystem.model.Doctor;
import com.simformsolutions.doctorappointmentsystem.model.Speciality;
import com.simformsolutions.doctorappointmentsystem.repository.DoctorRepository;
import com.simformsolutions.doctorappointmentsystem.repository.SpecialityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private SpecialityRepository specialityRepository;

    //GET
    public List<Doctor> getAllDoctors(){
        return doctorRepository.findAll();
    }
    public Doctor getDoctor(int id){
        return doctorRepository.findById(id).orElseThrow();
    }

    //POST
    public Doctor addDoctor(Doctor doctor){
        Speciality s = specialityRepository.findByTitle(doctor.getSpecialist());
        s.setDoctor(doctor);
        specialityRepository.save(s);
        return doctor;
    }

//    //UPDATE
//    Doctor updateDoctor(int id,Doctor doctor){
//        Doctor existingDoctor = doctorRepository.findById(id).orElse(null);
//        existingDoctor.setFirstName(doctor.getFirstName());
//        existingDoctor.setLastName(doctor.getLastName());
//        existingDoctor.setAge(doctor.getAge());
//
//        //EXTRA LOGIC
////		System.out.println(existingDoctor);
////
////		if ((doctor.getFirstName() != null && doctor.getFirstName() != ""))
////			existingDoctor.setFirstName(doctor.getFirstName());
////		if ((doctor.getLastName() != null && doctor.getLastName() != ""))
////			existingDoctor.setLastName(doctor.getLastName());
////		if (doctor.getAge() != 0)
////			existingDoctor.setAge(doctor.getAge());
//
//        return doctorRepository.save(existingDoctor);
//    }
//
//    //DELETE
//    String deleteDoctor(int doctorId);

}
