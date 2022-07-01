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
}
