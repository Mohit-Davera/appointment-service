package com.simformsolutions.appointment.controller;

import com.simformsolutions.appointment.dto.doctor.DoctorDetails;
import com.simformsolutions.appointment.dto.doctor.DoctorUpdateDetails;
import com.simformsolutions.appointment.service.DoctorService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/doctor")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping("/")
    public DoctorDetails registerDoctor(@Valid @RequestBody DoctorDetails doctorDetails) {
        return doctorService.saveDoctor(doctorDetails);
    }

}
