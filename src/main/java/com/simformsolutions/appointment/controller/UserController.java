package com.simformsolutions.appointment.controller;

import com.simformsolutions.appointment.dto.AppointmentDoctorDto;
import com.simformsolutions.appointment.dto.user.UserDetailsDto;
import com.simformsolutions.appointment.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/user")
@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/")
    public UserDetailsDto registerUser(@Valid @RequestBody UserDetailsDto userDetailsDto) {
        return userService.addUser(userDetailsDto);
    }


    @GetMapping("/")
    public ResponseEntity<List<AppointmentDoctorDto>> showAppointments(@RequestParam int userId) {
        return new Responder<List<AppointmentDoctorDto>>().apply(userService.getAppointments(userId));
    }

    @PostMapping("/{appointmentId}/cancel")
    public ResponseEntity<AppointmentDoctorDto> cancelAppointment(@PathVariable("appointmentId") int appointmentId, int userId) {
        return new Responder<AppointmentDoctorDto>().apply(userService.cancelAppointment(appointmentId));
    }

    @PostMapping("/{appointmentId}/reschedule")
    public ResponseEntity<AppointmentDoctorDto> rescheduleAppointment(@PathVariable("appointmentId") int appointmentId, @RequestParam int userId, @RequestParam(value = "days", required = false, defaultValue = "0") String days) {
        return new Responder<AppointmentDoctorDto>().apply(userService.rescheduleAppointment(appointmentId, userId, days));
    }

    @GetMapping("/{appointmentId}/doctors")
    public ResponseEntity<List<AppointmentDoctorDto>> availableDoctors(@PathVariable("appointmentId") int appointmentId,int userId) {
        return new Responder<List<AppointmentDoctorDto>>().apply(userService.getAvailableDoctors(appointmentId, userId));
    }

    @PostMapping("/changedoctor")
    public ResponseEntity<AppointmentDoctorDto> changeDoctor(@RequestBody AppointmentDoctorDto appointmentDoctorDto, @RequestParam("userId") int userId) {
        return new Responder<AppointmentDoctorDto>().apply(userService.changeDoctor(appointmentDoctorDto, userId));
    }
}
