package com.simformsolutions.doctorappointmentsystem.repository;

import com.simformsolutions.doctorappointmentsystem.model.Doctor;
import com.simformsolutions.doctorappointmentsystem.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule,Integer> {

    @Query(value = "SELECT * FROM schedule WHERE schedule.appointment_appointment_id = :appointmentId",nativeQuery = true)
    Optional<Schedule> getScheduleFromAppointmentId(@Param("appointmentId") int appointmentId);
}
