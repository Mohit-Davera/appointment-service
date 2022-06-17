package com.simformsolutions.doctorappointmentsystem.controller;

import com.simformsolutions.doctorappointmentsystem.dto.AppointmentDoctorDto;
import com.simformsolutions.doctorappointmentsystem.model.Doctor;
import com.simformsolutions.doctorappointmentsystem.model.User;
import com.simformsolutions.doctorappointmentsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/users")
@RestController
public class UserController {

    @Autowired
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/")
    public User registerUser(@Valid @RequestBody User user) {
        return userService.addUser(user);
    }


    @GetMapping("/")
    public ResponseEntity<List<AppointmentDoctorDto>> showAppointments(@RequestParam("userId") int userId) {
        return new Responder<List<AppointmentDoctorDto>>().apply(userService.getAppointments(userId));
    }

    @PostMapping("/{appointmentId}/cancel")
    public ResponseEntity<AppointmentDoctorDto> cancelAppointment(@PathVariable("appointmentId") int appointmentId, @RequestParam("userId") int userId) {
        return new Responder<AppointmentDoctorDto>().apply(userService.cancelAppointment(appointmentId));
    }

    @PostMapping("/{appointmentId}/reschedule")
    public ResponseEntity<AppointmentDoctorDto> rescheduleAppointment(@PathVariable("appointmentId") int appointmentId, @RequestParam("userId") int userId, @RequestParam(value = "days", required = false, defaultValue = "0") String days) {
        return new Responder<AppointmentDoctorDto>().apply(userService.rescheduleAppointments(appointmentId, userId, days));
    }

    @GetMapping("/{appointmentId}/doctors")
    public ResponseEntity<ArrayList<AppointmentDoctorDto>> availableDoctors(@PathVariable("appointmentId") int appointmentId, @RequestParam("userId") int userId) {
        return new Responder<ArrayList<AppointmentDoctorDto>>().apply(userService.availableDoctors(appointmentId, userId));
    }

    @PostMapping("/changedoctor")
    public ResponseEntity<AppointmentDoctorDto> changeDoctor(@RequestBody AppointmentDoctorDto appointmentDoctorDto,@RequestParam("userId") int userId) {
        return new Responder<AppointmentDoctorDto>().apply(userService.changeDoctor(appointmentDoctorDto, userId));
    }
}
