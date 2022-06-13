package com.simformsolutions.doctorappointmentsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentForm {

    private String issue;
    private String specialityTitle;
    private String patientName;
    private int age;
    private String gender;
}
