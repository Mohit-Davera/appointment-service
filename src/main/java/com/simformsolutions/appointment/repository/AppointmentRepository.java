package com.simformsolutions.appointment.repository;

import com.simformsolutions.appointment.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    @Query(value = "SELECT a.appointment_id, d.doctor_id as doctorId,d.first_name as firstName, d.last_name as lastName, d.experience as experience, a.speciality as specialist, a.end_time as bookingTime, a.date as bookingDate, a.status as status FROM appointments a JOIN doctors d ON d.doctor_id=a.doctor_id WHERE a.user_id = :userId"
            , nativeQuery = true)
    List<Tuple> findDetailsOfAppointments(@Param("userId") int userId);

    @Query(value = "SELECT a.appointment_id, d.doctor_id as doctorId,d.first_name as firstName, d.last_name as lastName, d.experience as experience, a.speciality as specialist, a.end_time as bookingTime, a.date as bookingDate, a.status as status FROM appointments a JOIN doctors d ON d.doctor_id=a.doctor_id WHERE a.appointment_id = :appointmentId"
            , nativeQuery = true)
    List<Tuple> findDetailsOfAppointment(@Param("appointmentId") int appointmentId);


    @Query(value = "SELECT d.doctor_id FROM appointments a JOIN doctors d ON d.doctor_id=a.doctor_id WHERE a.appointment_id = :appointmentId"
            , nativeQuery = true)
    int findDoctorByAppointmentId(@Param("appointmentId") int appointmentId);

}
