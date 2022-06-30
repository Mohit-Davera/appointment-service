package com.simformsolutions.appointment.controller;

import com.simformsolutions.appointment.dto.AppointmentDoctor;
import com.simformsolutions.appointment.dto.user.UserInformation;
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
    public UserInformation registerUser(@Valid @RequestBody UserInformation userInformation) {
        return userService.addUser(userInformation);
    }

    @GetMapping("/")
    public ResponseEntity<List<AppointmentDoctor>> showAppointments(@RequestParam int userId) {
        return new Responder<List<AppointmentDoctor>>().apply(userService.getAppointments(userId));
    }

    @PostMapping("/{appointmentId}/cancel")
    public ResponseEntity<AppointmentDoctor> cancelAppointment(@PathVariable("appointmentId") int appointmentId, @RequestParam int userId) {
        return new Responder<AppointmentDoctor>().apply(userService.cancelAppointment(appointmentId));
    }

    @PostMapping("/{appointmentId}/reschedule")
    public ResponseEntity<AppointmentDoctor> rescheduleAppointment(@PathVariable("appointmentId") int appointmentId, @RequestParam int userId, @RequestParam(value = "days", required = false, defaultValue = "0") String days) {
        return new Responder<AppointmentDoctor>().apply(userService.rescheduleAppointment(appointmentId, userId, days));
    }

    @GetMapping("/{appointmentId}/doctors")
    public ResponseEntity<List<AppointmentDoctor>> availableDoctors(@PathVariable("appointmentId") int appointmentId, @RequestParam int userId) {
        return new Responder<List<AppointmentDoctor>>().apply(userService.getAvailableDoctors(appointmentId, userId));
    }

    @PostMapping("/change-doctor")
    public ResponseEntity<AppointmentDoctor> changeDoctor(@RequestBody AppointmentDoctor appointmentDoctor, @RequestParam("userId") int userId) {
        return new Responder<AppointmentDoctor>().apply(userService.changeDoctor(appointmentDoctor, userId));
    }
}
