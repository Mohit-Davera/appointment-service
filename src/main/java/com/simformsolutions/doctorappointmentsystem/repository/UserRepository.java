package com.simformsolutions.doctorappointmentsystem.repository;

import com.simformsolutions.doctorappointmentsystem.model.Appointment;
import com.simformsolutions.doctorappointmentsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
}
