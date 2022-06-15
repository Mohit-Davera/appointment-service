package com.simformsolutions.doctorappointmentsystem.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@ToString
public class AppointmentDoctorDto {

    @JsonIgnore
    private int doctorId;
    private String doctorName;
    private int experience;
    private String specialist;
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime bookingTime;
    @JsonFormat(pattern="dd/MM/yyyy",shape = JsonFormat.Shape.STRING)
    private LocalDate bookedDate;

    public AppointmentDoctorDto(int doctorId,String doctorName, int experience, String specialist, LocalTime bookingTime, LocalDate bookedDate) {
        this.doctorId=doctorId;
        this.doctorName = "Dr. "+ doctorName;
        this.experience = experience;
        this.specialist = specialist;
        this.bookingTime = bookingTime;
        this.bookedDate = bookedDate;
    }

    public int retriveBookingTimeInHour(){
        return this.bookingTime.getHour();
    }
}
