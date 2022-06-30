package com.simformsolutions.appointment.controller;

import com.simformsolutions.appointment.dto.speciality.SpecialityTitle;
import com.simformsolutions.appointment.service.SpecialityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/speciality")
@RestController
public class SpecialityController {

    private final SpecialityService specialityService;

    public SpecialityController(SpecialityService specialityService) {
        this.specialityService = specialityService;
    }

    @GetMapping("/")
    public ResponseEntity<SpecialityTitle> showSpecialities() {
        return new Responder<SpecialityTitle>().apply(specialityService.showSpecialities());

    }

    @PostMapping("/")
    public ResponseEntity<SpecialityTitle> addSpecialities(@RequestBody SpecialityTitle specialityTitle) {
        return new Responder<SpecialityTitle>().apply(specialityService.saveNewSpecialities(specialityTitle.getTitles()));
    }
}
