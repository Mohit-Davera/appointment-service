package com.simformsolutions.appointment.service;

import com.simformsolutions.appointment.dto.speciality.SpecialityTitleDto;
import com.simformsolutions.appointment.model.Speciality;
import com.simformsolutions.appointment.repository.SpecialityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecialityService {

    @Autowired
    private final SpecialityRepository specialityRepository;

    public SpecialityService(SpecialityRepository specialityRepository) {
        this.specialityRepository = specialityRepository;
    }

    public SpecialityTitleDto showSpecialities(){
        return new SpecialityTitleDto(specialityRepository.findAll().stream().map(Speciality::getTitle).toList());
    }

    public SpecialityTitleDto saveNewSpecialities(List<String> titles){
       specialityRepository.saveAll(
                titles.stream().filter( title ->
                        !specialityRepository.existsByTitle(title.toLowerCase())).map(Speciality::new).toList());
       return new SpecialityTitleDto(titles);
    }
}
