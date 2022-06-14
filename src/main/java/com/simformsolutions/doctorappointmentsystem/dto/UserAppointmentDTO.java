package com.simformsolutions.doctorappointmentsystem.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.simformsolutions.doctorappointmentsystem.model.Appointment;
import com.simformsolutions.doctorappointmentsystem.model.Doctor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAppointmentDTO {
    private Appointment appointment;

    @JsonIgnoreProperties({"phoneNumber","city","degree","collegeName","specialist","appointments"})
    private Doctor doctor;
}
