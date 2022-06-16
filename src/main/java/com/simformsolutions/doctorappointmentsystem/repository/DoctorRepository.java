package com.simformsolutions.doctorappointmentsystem.repository;

import com.simformsolutions.doctorappointmentsystem.projection.DoctorInter;
import com.simformsolutions.doctorappointmentsystem.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository  extends JpaRepository<Doctor,Integer> {
    @Query(value = "SELECT d.doctor_id as doctorId " +
            "FROM doctor d WHERE d.speciality_id =:specialityId"
            ,nativeQuery = true)
    List<DoctorInter> findDoctorsWithSpeciality(@Param("specialityId") int specialityId);

}
