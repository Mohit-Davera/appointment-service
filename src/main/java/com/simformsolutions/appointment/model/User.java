package com.simformsolutions.appointment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
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
    @OneToMany(targetEntity = Appointment.class,cascade=CascadeType.ALL)
    @JoinColumn(name = "userId",referencedColumnName = "userId")
    private List<Appointment> appointments;

    public void setAppointment(Appointment a){
        appointments.add(a);
    }

}
