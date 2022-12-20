package com.simformsolutions.appointment.service.oauth;


import org.springframework.security.core.AuthenticatedPrincipal;

public interface CryptoPrincipal extends AuthenticatedPrincipal{

	String getUsername();
	String getFirstName();
	String getLastName();
	String getFullName();
	String getEmail();
	
}
