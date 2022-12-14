package com.simformsolutions.appointment.dto.doctor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Transient;
import javax.validation.constraints.*;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorUpdateDetails {

    @NotNull
    private int doctorId;

    private String firstName;

    private String lastName;

    private boolean isEnabled;

    private String password;

    private String phoneNumber;

    private String email;

    private String city;

    private String degree;

    private String collegeName;

    private int experience;

    private String speciality;

    private LocalTime entryTime;

    private LocalTime exitTime;
}
