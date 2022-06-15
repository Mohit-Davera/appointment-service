package com.simformsolutions.doctorappointmentsystem.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalTime;
@Data
@AllArgsConstructor@NoArgsConstructor
@ToString
public class DoctorDetails {
    private int doctorId;
    private int experience;
    private LocalTime startTime;
    private LocalTime exitTime;
}
