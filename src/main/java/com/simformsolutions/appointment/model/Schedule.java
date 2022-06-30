package com.simformsolutions.appointment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "schedules")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private LocalTime appointedTime;
    private LocalDate bookedDate;
    @OneToOne(cascade = CascadeType.ALL)
    private Doctor doctor;
    @OneToOne(cascade = CascadeType.ALL)
    private User user;
    @OneToOne(cascade = CascadeType.ALL)
    private Appointment appointment;

    public Schedule(LocalTime appointedTime, LocalDate bookedDate, Doctor doctor, User user, Appointment appointment) {
        this.appointedTime = appointedTime;
        this.bookedDate = bookedDate;
        this.doctor = doctor;
        this.user = user;
        this.appointment = appointment;
    }
}
