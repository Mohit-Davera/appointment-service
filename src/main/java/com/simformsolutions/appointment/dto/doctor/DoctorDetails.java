package com.simformsolutions.appointment.dto.doctor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Transient;
import javax.validation.constraints.*;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorDetails {

    private int doctorId;

    @NotEmpty(message = "Please Enter First Name")
    private String firstName;

    @NotEmpty(message = "Please Enter Last Name")
    private String lastName;

    @Transient
    @JsonIgnore
    private boolean isEnabled;

    @Size(min = 5, message = "Please Enter Password With More Than 5 Letters")
    @NotEmpty(message = "Please Enter Password")
    private String password;

    @NotEmpty(message = "Please Enter Phone Number")
    @Size(min = 10, max = 10, message = "Enter 10 Digit Number")
    private String phoneNumber;

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
    @Min(value = 2, message = "Required Experience Is 2 Years")
    private int experience;

    @NotNull(message = "Please Enter Speciality")
    private String speciality;

    @DateTimeFormat(pattern = "HH:mm:ss")
    @NotNull(message = "Please Enter Entry Time")
    private LocalTime entryTime;

    @DateTimeFormat(pattern = "HH:mm:ss")
    @NotNull(message = "Please Enter Exit Time")
    private LocalTime exitTime;

    public DoctorDetails(int doctorId, String firstName, String lastName, String phoneNumber, String email, String city, String degree, String collegeName, int experience, String speciality, LocalTime entryTime, LocalTime exitTime) {
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
    }
}
