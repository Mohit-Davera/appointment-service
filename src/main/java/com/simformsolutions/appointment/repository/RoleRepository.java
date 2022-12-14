package com.simformsolutions.appointment.repository;

import com.simformsolutions.appointment.model.Role;
import com.simformsolutions.appointment.model.RoleDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Integer> {
}
