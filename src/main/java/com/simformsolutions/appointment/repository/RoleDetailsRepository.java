package com.simformsolutions.appointment.repository;

import com.simformsolutions.appointment.model.RoleDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleDetailsRepository extends JpaRepository<RoleDetails,Integer> {

    Optional<RoleDetails> findByEmail(String email);

    @Query(value = "SELECT * FROM role_details WHERE role_details.role_id = :roleId", nativeQuery = true)
    List<RoleDetails> findRoleDetailsByRoleId(@Param("roleId") int roleId);

    @Query(value = "SELECT r.name FROM role_details rd JOIN roles r ON rd.role_id = r.role_id WHERE rd.email = :email", nativeQuery = true)
    String findRoleNameFromEmail(@Param("email") String email);
}