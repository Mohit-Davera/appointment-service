package com.simformsolutions.doctorappointmentsystem.controller;

import com.simformsolutions.doctorappointmentsystem.model.Doctor;
import com.simformsolutions.doctorappointmentsystem.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/doctors")
public class DoctorController {

    @Autowired
    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping("/")
    public Doctor saveDoctor(@Valid @RequestBody Doctor d){
        return doctorService.addDoctor(d);
    }

}
