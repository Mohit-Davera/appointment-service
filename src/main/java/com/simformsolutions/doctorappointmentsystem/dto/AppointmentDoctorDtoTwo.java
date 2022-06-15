package com.simformsolutions.doctorappointmentsystem.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AppointmentDoctorDtoTwo {

    @JsonIgnore
    private int doctorId;
    private String firstName;
    private String lastName;
    private int experience;
    private String specialist;
    private LocalTime bookingTime;
    @JsonFormat(pattern="dd/MM/yyyy",shape = JsonFormat.Shape.STRING)
    private LocalDate bookedDate;

}
