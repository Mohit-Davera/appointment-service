package com.simformsolutions.doctorappointmentsystem.repository;

import com.simformsolutions.doctorappointmentsystem.Projection.DoctorDetails;
import com.simformsolutions.doctorappointmentsystem.Projection.DoctorInter;
import com.simformsolutions.doctorappointmentsystem.model.Doctor;
import com.simformsolutions.doctorappointmentsystem.model.Speciality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpecialityRepository extends JpaRepository<Speciality,Integer> {

    Speciality findByTitle(String title);

    @Query(value = "SELECT d.doctor_id as doctorId," +
            "d.entry_time as entryTime,"+
            "d.exit_time as exitTime,"+
            "d.experience as experience "+
            "FROM doctor d WHERE d.speciality_id =:specialityId"
    ,nativeQuery = true)
    List<DoctorInter> findDoctorsWithSpeciality(@Param("specialityId") int specialityId);

    boolean existsByTitle(String title);
}
