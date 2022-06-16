package com.simformsolutions.doctorappointmentsystem.repository;

import com.simformsolutions.doctorappointmentsystem.model.Doctor;
import com.simformsolutions.doctorappointmentsystem.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule,Integer> {
}
