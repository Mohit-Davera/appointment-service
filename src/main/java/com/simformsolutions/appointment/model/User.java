package com.simformsolutions.appointment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.simformsolutions.appointment.enums.Provider;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private int userId;
    private String name;

    @Column(unique = true)
    private String email;

    private String number;

    private String password;
    @JsonIgnore
    @OneToMany(targetEntity = Appointment.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private List<Appointment> appointments;

    private boolean enabled;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    public User(String name, String email, String number, String password) {
        this.name = name;
        this.email = email;
        this.number = number;
        this.password = password;
    }

    public User(int userId, String name, String email, String number, String password) {
        this(name, email, number, password);
        this.userId = userId;

    }

    public void addAppointment(Appointment a) {
        this.appointments.add(a);
    }
}
