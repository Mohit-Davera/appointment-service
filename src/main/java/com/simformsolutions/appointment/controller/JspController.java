package com.simformsolutions.appointment.controller;

import com.simformsolutions.appointment.model.Speciality;
import com.simformsolutions.appointment.model.User;
import com.simformsolutions.appointment.repository.SpecialityRepository;
import com.simformsolutions.appointment.repository.UserRepository;
import com.simformsolutions.appointment.service.oauth.CryptoPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@Slf4j
public class JspController {

    @Autowired
    private SpecialityRepository specialityRepository;

    @Autowired
    private UserRepository userRepository;
    @GetMapping("/home")
    public ModelAndView home(Principal principal){
        List<Speciality> specialities = specialityRepository.findAll();
        String principalName = principal.getName();
        log.info(principalName);
        Optional<User> optionalUser = userRepository.findByEmail(principalName);
        log.info(optionalUser.toString());
        ModelAndView modelAndView =  new ModelAndView();
        modelAndView.addObject("specialities",specialities);
        modelAndView.addObject("name", principalName);
        return modelAndView;
    }
}