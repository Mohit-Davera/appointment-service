package com.simformsolutions.appointment.controller;

import com.simformsolutions.appointment.dto.AppointmentDoctor;
import com.simformsolutions.appointment.dto.appointment.AppointmentDetails;
import com.simformsolutions.appointment.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RequestMapping("/appointment")
@RestController
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/book")
    public ResponseEntity<AppointmentDoctor> bookAppointment(@Valid @RequestBody AppointmentDetails appointmentDetails, @RequestParam int userId) {
        return new Responder<AppointmentDoctor>().apply(appointmentService.saveAppointment(appointmentDetails, userId));

    }
}
