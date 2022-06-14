package com.simformsolutions.doctorappointmentsystem.repository;

import com.simformsolutions.doctorappointmentsystem.model.Doctor;
import com.simformsolutions.doctorappointmentsystem.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule,Integer> {
    List<Schedule> findByDoctor(Doctor doctor);
    boolean existsByDoctor(Doctor doctor);
    boolean existsByDoctor(Optional<Doctor> doctor);
}
