package com.simformsolutions.doctorappointmentsystem.Projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalTime;
@Data
@AllArgsConstructor@NoArgsConstructor
@ToString
public class DoctorDetailsDto {
    private int doctorId;
    private LocalTime startTime;
    private LocalTime exitTime;
    private int experience;
}

