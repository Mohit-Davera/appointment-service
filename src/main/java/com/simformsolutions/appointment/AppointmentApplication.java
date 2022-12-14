package com.simformsolutions.appointment;

import com.simformsolutions.appointment.enums.CustomRole;
import com.simformsolutions.appointment.model.Role;
import com.simformsolutions.appointment.repository.RoleDetailsRepository;
import com.simformsolutions.appointment.repository.RoleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class AppointmentApplication {

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    RoleDetailsRepository roleDetailsRepository;

    public static void main(String[] args) {
        SpringApplication.run(AppointmentApplication.class, args);
    }

    @PostConstruct
    public void init() {
        roleRepository.save(new Role(1, CustomRole.USER.getLabel(), roleDetailsRepository.findRoleDetailsByRoleId(1)));
        roleRepository.save(new Role(2, CustomRole.DOCTOR.getLabel(), roleDetailsRepository.findRoleDetailsByRoleId(2)));
        roleRepository.save(new Role(3, CustomRole.ADMIN.getLabel(), roleDetailsRepository.findRoleDetailsByRoleId(3)));
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
