package com.simformsolutions.appointment.controller;

import com.simformsolutions.appointment.dto.speciality.SpecialityTitle;
import com.simformsolutions.appointment.repository.RoleDetailsRepository;
import com.simformsolutions.appointment.service.SpecialityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequestMapping("/speciality")
@RestController
public class SpecialityController {

    private final SpecialityService specialityService;

    private final RoleDetailsRepository roleDetailsRepository;

    public SpecialityController(SpecialityService specialityService, RoleDetailsRepository roleDetailsRepository) {
        this.specialityService = specialityService;
        this.roleDetailsRepository = roleDetailsRepository;
    }

    @GetMapping("/")
    public ResponseEntity<SpecialityTitle> showSpecialities(Principal principal) {
        System.out.println(principal.getName());
        return new Responder<SpecialityTitle>().apply(specialityService.showSpecialities());

    }

    @PostMapping("/")
    public ResponseEntity<SpecialityTitle> addSpecialities(@RequestBody SpecialityTitle specialityTitle) {
        return new Responder<SpecialityTitle>().apply(specialityService.saveNewSpecialities(specialityTitle.getTitles()));
    }

    @GetMapping("/testing")
    public void testing() {
        //System.out.println(roleDetailsRepository.findRoleDetailsByRoleId(1));
        System.out.println(roleDetailsRepository.findRoleNameFromEmail("mohit1@gmail.com"));
        //System.out.println(roleDetailsRepository.findAll());
    }
}
