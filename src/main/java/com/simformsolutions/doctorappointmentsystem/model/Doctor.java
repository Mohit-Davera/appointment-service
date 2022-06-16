package com.simformsolutions.doctorappointmentsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalTime;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private int doctorId;

    @NotEmpty(message = "Please Enter First Name")
    private String firstName;

    @NotEmpty(message = "Please Enter Last Name")
    private String lastName;

    @NotEmpty(message = "Please Enter Phone Number")
    @Size(min = 10,max = 10,message = "Enter 10 Digit Number")
    private String phoneNumber;

    @Column(unique = true)
    @Email
    @NotEmpty(message = "Please Enter Email")
    private String email;

    @NotEmpty(message = "Please Enter City")
    private String city;

    @NotEmpty(message = "Please Enter Degree")
    private String degree;

    @NotEmpty(message = "Please Enter College Name")
    private String collegeName;

    @NotNull
    @Min(value = 2,message = "Required Experience Is 2 Years")
    private int experience;

    @Transient
    private String specialist;

    @NotNull
    private LocalTime entryTime;
    @NotNull
    private LocalTime exitTime;

    @JsonIgnore
    @OneToMany(targetEntity = Appointment.class,cascade = CascadeType.ALL)
    @JoinColumn(name = "doctorId",referencedColumnName = "doctorId")
    private List<Appointment> appointments;
    @JsonIgnore
    public Appointment setAppointments(Appointment appointment) {
        appointments.add(appointment);
        return appointment;
    }
    @JsonIgnore
    public Appointment getLastAppointment() {
        return appointments.get(appointments.size()-1);
    }
}
