package com.simformsolutions.appointment.service.details;

import com.simformsolutions.appointment.model.User;
import com.simformsolutions.appointment.repository.RoleDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@ComponentScan("com.simformsolutions.appointment.repository")
public class CustomUserDetails implements UserDetails {

    private final User user;

    private RoleDetailsRepository roleDetailsRepository;

    public CustomUserDetails(User user, RoleDetailsRepository roleDetailsRepository) {
        this.user = user;
        this.roleDetailsRepository = roleDetailsRepository;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        roleDetailsRepository.findByEmail(user.getEmail());
        authorities.add(new SimpleGrantedAuthority(roleDetailsRepository.findRoleNameFromEmail(user.getEmail())));
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }
}
