package com.simformsolutions.doctorappointmentsystem.service;

import com.simformsolutions.doctorappointmentsystem.model.Speciality;
import com.simformsolutions.doctorappointmentsystem.repository.SpecialityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpecialityService {

    @Autowired
    private SpecialityRepository specialityRepository;

    public List<Speciality> showSpecialities(){
        return specialityRepository.findAll();
    }

    public List<Speciality> saveNewSpecialities(List<String> titles){
       return specialityRepository.saveAll(
                titles.stream().filter( title ->
                        !specialityRepository.existsByTitle(title.toLowerCase())).map(Speciality::new).collect(Collectors.toList()));
    }
}
