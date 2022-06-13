package com.simformsolutions.doctorappointmentsystem.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int appointmentId;

    private String specialityTitle;
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime time;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate date;
    private String issue;

}
