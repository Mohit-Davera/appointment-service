package com.simformsolutions.appointment.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.simformsolutions.appointment.enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Appointment {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int appointmentId;

    private String speciality;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime endTime;

    @JsonFormat(pattern="dd/MM/yyyy",shape = JsonFormat.Shape.STRING)
    private LocalDate date;

    private String patientName;

    private String issue;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    public Appointment(int appointmentId) {
        this.appointmentId = appointmentId;
    }
}
