package com.simformsolutions.doctorappointmentsystem.controller;

import com.simformsolutions.doctorappointmentsystem.dto.AppointmentDoctorDtoTwo;
import com.simformsolutions.doctorappointmentsystem.model.Appointment;
import com.simformsolutions.doctorappointmentsystem.model.Speciality;
import com.simformsolutions.doctorappointmentsystem.model.User;
import com.simformsolutions.doctorappointmentsystem.repository.UserRepository;
import com.simformsolutions.doctorappointmentsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/users")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/")
    public User registerUser(@Valid @RequestBody User user){
        return userService.addUser(user);
    }


    @GetMapping("/")
    public ResponseEntity<List<AppointmentDoctorDtoTwo>> showAppointments(@RequestParam("userId") int userId){
        return new Responder<List<AppointmentDoctorDtoTwo>>().apply(userService.getAppointments(userId));
    }

}
