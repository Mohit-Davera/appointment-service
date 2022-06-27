package com.simformsolutions.appointment.repository;

import com.simformsolutions.appointment.projection.DoctorView;
import com.simformsolutions.appointment.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Integer> {
    @Query(value = "SELECT d.doctor_id as doctorId " +
            "FROM doctor d WHERE d.speciality_id =:specialityId"
            , nativeQuery = true)
    List<DoctorView> findDoctorsIdWithSpeciality(@Param("specialityId") int specialityId);

    @Query(value = "SELECT * " +
            "FROM doctor d WHERE d.speciality_id =:specialityId"
            , nativeQuery = true)
    List<Doctor> findDoctorsWithSpeciality(@Param("specialityId") int specialityId);


}
