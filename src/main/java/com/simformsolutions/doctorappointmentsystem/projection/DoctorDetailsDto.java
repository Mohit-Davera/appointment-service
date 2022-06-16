package com.simformsolutions.doctorappointmentsystem.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
@Data
@AllArgsConstructor@NoArgsConstructor
@ToString
public class DoctorDetailsDto {
    private int doctorId;
    private String firstName;
    private String lastName;
    private int experience;
}

