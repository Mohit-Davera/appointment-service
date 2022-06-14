package com.simformsolutions.doctorappointmentsystem.controller;

import com.simformsolutions.doctorappointmentsystem.model.Appointment;
import com.simformsolutions.doctorappointmentsystem.model.Speciality;
import com.simformsolutions.doctorappointmentsystem.repository.DoctorRepository;
import com.simformsolutions.doctorappointmentsystem.repository.SpecialityRepository;
import com.simformsolutions.doctorappointmentsystem.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RequestMapping("/appointment")
@RestController
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @RequestMapping("/register")
    public ResponseEntity<Object> registerAppointment(@Valid @RequestBody Appointment appointment, @RequestParam("userId") int userId){
        return new Responder<>().apply(appointmentService.bookAppointment(appointment,userId));

    }
}
