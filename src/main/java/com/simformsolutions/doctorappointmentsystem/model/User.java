package com.simformsolutions.doctorappointmentsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
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
    @Email(message = "Please Enter Valid Email")
    private String email;

    @Size(max = 10,min = 10,message = "Please Enter Appropriate Number")
    private String number;

    @JsonIgnore
    @OneToMany(targetEntity = Appointment.class,cascade=CascadeType.ALL)
    @JoinColumn(name = "userId",referencedColumnName = "userId")
    private List<Appointment> appointments;

    public void setAppointment(Appointment a){
        appointments.add(a);
    }

}
