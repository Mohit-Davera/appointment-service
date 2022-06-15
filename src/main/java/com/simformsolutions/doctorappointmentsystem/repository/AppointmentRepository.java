package com.simformsolutions.doctorappointmentsystem.repository;

import com.simformsolutions.doctorappointmentsystem.dto.AppointmentDoctorDto;
import com.simformsolutions.doctorappointmentsystem.dto.AppointmentDoctorDtoTwo;
import com.simformsolutions.doctorappointmentsystem.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment,Integer> {

    @Query(value = "SELECT d.doctor_id as doctorId," +
            "d.first_name as firstName," +
            "d.last_name as lastName," +
            "d.experience as experience," +
            "a.speciality as specialist," +
            "a.time as bookingTime," +
            "a.date as bookingDate" +
            "FROM appointment_system.appointment a JOIN appointment_system.doctor d ON d.doctor_id=a.doctor_id " +
            "WHERE a.user_id = :userId"
            ,nativeQuery = true)
    List<AppointmentDoctorDtoTwo> findDetailsOfAppointments(@Param("userId") int userId);

}
