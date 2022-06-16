package com.simformsolutions.doctorappointmentsystem.controller;

import com.simformsolutions.doctorappointmentsystem.dto.SpecialityTitleDTO;
import com.simformsolutions.doctorappointmentsystem.model.Speciality;
import com.simformsolutions.doctorappointmentsystem.service.SpecialityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/specialities")
@RestController
public class SpecialtyController {

    @Autowired
    private final SpecialityService specialityService;

    public SpecialtyController(SpecialityService specialityService) {
        this.specialityService = specialityService;
    }

    @GetMapping("/")
    public ResponseEntity<List<Speciality>> showSpecialities(){
        return new Responder<List<Speciality>>().apply(specialityService.showSpecialities());

    }

    @PostMapping("/")
    public ResponseEntity<List<Speciality>> addSpecialities(@RequestBody SpecialityTitleDTO dto){
        return new Responder<List<Speciality>>().apply(specialityService.saveNewSpecialities(dto.getTitles()));
    }
}
