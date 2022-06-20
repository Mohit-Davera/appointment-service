package com.simformsolutions.appointment.dto.doctor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorDetailsDto {


    private int doctorId;

    @NotEmpty(message = "Please Enter First Name")
    private String firstName;

    @NotEmpty(message = "Please Enter Last Name")
    private String lastName;

    @NotEmpty(message = "Please Enter Phone Number")
    @Size(min = 10,max = 10,message = "Enter 10 Digit Number")
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
    @Min(value = 2,message = "Required Experience Is 2 Years")
    private int experience;

    @NotNull(message = "Please Enter Speciality")
    private String specialist;

    @NotNull(message = "Please Enter Entry Time")
    private LocalTime entryTime;
    @NotNull(message = "Please Enter Exit Time")
    private LocalTime exitTime;
}
