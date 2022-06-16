package com.simformsolutions.doctorappointmentsystem.controller;

import com.simformsolutions.doctorappointmentsystem.model.Appointment;
import com.simformsolutions.doctorappointmentsystem.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RequestMapping("/appointment")
@RestController
public class AppointmentController {

    @Autowired
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/register")
    public ResponseEntity<Object> registerAppointment(@Valid @RequestBody Appointment appointment, @RequestParam("userId") int userId){
        return new Responder<>().apply(appointmentService.bookAppointment(appointment,userId));

    }
}
