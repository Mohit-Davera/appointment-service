package com.simformsolutions.appointment.repository;

import com.simformsolutions.appointment.model.Speciality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecialityRepository extends JpaRepository<Speciality, Integer> {

    Speciality findByTitle(String title);

    boolean existsByTitle(String title);
}
