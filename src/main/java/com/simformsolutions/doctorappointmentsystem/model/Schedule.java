package com.simformsolutions.doctorappointmentsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private LocalTime appointedTime;
    private LocalDate bookedDate;

    @OneToOne
    private Doctor doctor;

    @OneToOne
    private User user;

    @OneToOne
    private Appointment appointment;
}
