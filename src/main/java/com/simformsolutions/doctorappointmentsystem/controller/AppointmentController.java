package com.simformsolutions.doctorappointmentsystem.controller;

import com.simformsolutions.doctorappointmentsystem.model.Appointment;
import com.simformsolutions.doctorappointmentsystem.repository.SpecialityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/appointment")
@RestController
public class AppointmentController {

    @Autowired
    private SpecialityRepository specialityRepository;

    @RequestMapping("/register")
    public String registerAppointment(@RequestBody Appointment appointment, @RequestParam("userId") int userId){
        String title = appointment.getSpecialityTitle().toLowerCase();
        if(!specialityRepository.existsByTitle(title)){
            return "Enter Appropriate Specialty";
        }
    System.out.println(
            specialityRepository.findDoctorsWithSpeciality(specialityRepository.findByTitle(title).getSpecialityId())
    );
        return "";
    }
}
