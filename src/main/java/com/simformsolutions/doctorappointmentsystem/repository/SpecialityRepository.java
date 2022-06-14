package com.simformsolutions.doctorappointmentsystem.repository;

import com.simformsolutions.doctorappointmentsystem.Projection.DoctorDetailsDto;
import com.simformsolutions.doctorappointmentsystem.Projection.DoctorInter;
import com.simformsolutions.doctorappointmentsystem.model.Speciality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecialityRepository extends JpaRepository<Speciality,Integer> {

    Speciality findByTitle(String title);

    boolean existsByTitle(String title);
}
