package com.simformsolutions.appointment.repository;

import com.simformsolutions.appointment.model.RoleDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleDetailsRepository extends JpaRepository<RoleDetails, Integer> {

    Optional<RoleDetails> findByEmail(String email);
}
