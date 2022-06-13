package com.simformsolutions.doctorappointmentsystem.controller;

import com.simformsolutions.doctorappointmentsystem.dto.SpecialityTitleDTO;
import com.simformsolutions.doctorappointmentsystem.model.Speciality;
import com.simformsolutions.doctorappointmentsystem.repository.SpecialityRepository;
import com.simformsolutions.doctorappointmentsystem.service.SpecialityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/specialities")
@RestController
public class SpecialtyController {

    @Autowired
    private SpecialityService specialityService;

    @GetMapping("/")
    public ResponseEntity<List<Speciality>> showSpecialities(){
        return new Responder<List<Speciality>>().apply(specialityService.showSpecialities());

    }

    @PostMapping("/")
    public ResponseEntity<List<Speciality>> addSpecialities(@RequestBody SpecialityTitleDTO dto){
        return new Responder<List<Speciality>>().apply(specialityService.saveNewSpecialities(dto.getTitles()));
    }
}
