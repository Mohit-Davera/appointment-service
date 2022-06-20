package com.simformsolutions.appointment.controller;

import com.simformsolutions.appointment.dto.speciality.SpecialityTitleDto;
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
    public ResponseEntity<SpecialityTitleDto> showSpecialities(){
        return new Responder<SpecialityTitleDto>().apply(specialityService.showSpecialities());

    }

    @PostMapping("/")
    public ResponseEntity<SpecialityTitleDto> addSpecialities(@RequestBody SpecialityTitleDto specialityTitleDto){
        return new Responder<SpecialityTitleDto>().apply(specialityService.saveNewSpecialities(specialityTitleDto.getTitles()));
    }
}
