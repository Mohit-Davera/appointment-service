package com.simformsolutions.appointment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private int doctorId;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    @Column(unique = true)
    private String email;

    private String city;

    private String degree;

    private String collegeName;

    private int experience;

    @Transient
    private String speciality;

    private LocalTime entryTime;

    private LocalTime exitTime;

    private String password;

    private boolean isEnabled;

    @JsonIgnore
    @OneToMany(targetEntity = Appointment.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "doctorId", referencedColumnName = "doctorId")
    private List<Appointment> appointments;

    public Doctor(int doctorId) {
        this.doctorId = doctorId;
    }

    @JsonIgnore
    public void addAppointment(Appointment appointment) {
        if (this.appointments == null)
            this.appointments = new ArrayList<>(List.of(appointment));
        else
            this.appointments.add(appointment);
    }

    public Doctor(int doctorId, String firstName, String lastName, String phoneNumber, String email, String city, String degree, String collegeName, int experience, String speciality, LocalTime entryTime, LocalTime exitTime, List<Appointment> appointments) {
        this.doctorId = doctorId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.city = city;
        this.degree = degree;
        this.collegeName = collegeName;
        this.experience = experience;
        this.speciality = speciality;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.appointments = appointments;
    }
}
