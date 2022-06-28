package com.simformsolutions.appointment.service.details;

import com.simformsolutions.appointment.excepetion.EmailNotFoundException;
import com.simformsolutions.appointment.model.User;
import com.simformsolutions.appointment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String email)
			throws UsernameNotFoundException {
		Optional<User> optionalUser = userRepository.findByEmail(email);
		
		if (optionalUser.isEmpty()) throw new EmailNotFoundException();
		
		return new CustomUserDetails(optionalUser.get());
	}

}
