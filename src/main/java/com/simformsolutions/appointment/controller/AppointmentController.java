package com.simformsolutions.appointment.controller;

import com.simformsolutions.appointment.dto.AppointmentDoctorDto;
import com.simformsolutions.appointment.dto.appointment.AppointmentDetailsDto;
import com.simformsolutions.appointment.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RequestMapping("/appointment")
@RestController
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/book")
    public ResponseEntity<AppointmentDoctorDto> bookAppointment(@Valid @RequestBody AppointmentDetailsDto appointmentDetailsDto, @RequestParam int userId) {
        return new Responder<AppointmentDoctorDto>().apply(appointmentService.saveAppointment(appointmentDetailsDto, userId));

    }

    @PostMapping("/testing")
    public ResponseEntity<List<AppointmentDoctorDto>> testingConverter(@RequestParam int userId) {
        return new Responder<List<AppointmentDoctorDto>>().apply(appointmentService.customConverterTesting(userId));
    }
}
