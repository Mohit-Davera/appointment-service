package com.simformsolutions.doctorappointmentsystem.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.simformsolutions.doctorappointmentsystem.enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Appointment {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int appointmentId;

    @NotEmpty(message = "Enter Speciality ")
    private String speciality;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime time;

    @NotNull(message = "Please Enter Appointment Date")
    @JsonFormat(pattern="dd/MM/yyyy",shape = JsonFormat.Shape.STRING)
    @FutureOrPresent(message = "Please Enter Today's Date or Future Date")
    private LocalDate date;

    @NotEmpty(message = "Please Enter Patient Name")
    private String patientName;

    @NotEmpty(message = "Please Enter Your Issue")
    private String issue;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;
    
}
