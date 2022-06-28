package com.simformsolutions.appointment.repository;

import com.simformsolutions.appointment.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

    @Query(value = "SELECT * FROM schedules WHERE schedule.appointment_appointment_id = :appointmentId", nativeQuery = true)
    Optional<Schedule> getScheduleFromAppointmentId(@Param("appointmentId") int appointmentId);
}
