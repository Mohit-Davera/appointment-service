package com.simformsolutions.doctorappointmentsystem.service;

import com.simformsolutions.doctorappointmentsystem.dto.AppointmentDoctorDtoTwo;
import com.simformsolutions.doctorappointmentsystem.model.User;
import com.simformsolutions.doctorappointmentsystem.repository.AppointmentRepository;
import com.simformsolutions.doctorappointmentsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class
UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    public List<AppointmentDoctorDtoTwo> getAppointments(int userId){
        return appointmentRepository.findDetailsOfAppointments(userId);
    }

    public User addUser(User user){
        return userRepository.save(user);
    }

}
