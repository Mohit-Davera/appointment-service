package com.simformsolutions.doctorappointmentsystem.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.simformsolutions.doctorappointmentsystem.enums.AppointmentStatus;
import com.simformsolutions.doctorappointmentsystem.model.Appointment;
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

    private int appointmentId;
    @JsonIgnore
    private int doctorId;
    private String doctorName;
    private int experience;
    private String specialist;
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime bookingTime;
    @JsonFormat(pattern="dd/MM/yyyy",shape = JsonFormat.Shape.STRING)
    private LocalDate bookedDate;
    private String status;

    public AppointmentDoctorDto(int doctorId,String doctorName, int experience, String specialist, LocalTime bookingTime, LocalDate bookedDate) {
        this.doctorId=doctorId;
        this.doctorName = "Dr. "+ doctorName;
        this.experience = experience;
        this.specialist = specialist;
        this.bookingTime = bookingTime;
        this.bookedDate = bookedDate;
    }
    public AppointmentDoctorDto(int appointmentId,int doctorId,String doctorName, int experience, String specialist, LocalTime bookingTime, LocalDate bookedDate,String status) {
        this(doctorId, doctorName,experience,specialist, bookingTime,bookedDate);
        this.status = status;
        this.appointmentId = appointmentId;
    }

    public int retrieveBookingTimeInHour(){
        return this.bookingTime.getHour();
    }
}
