package com.simformsolutions.appointment.dto.appointment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDetailsDto {

    @NotEmpty(message = "Enter Speciality ")
    private String speciality;

    @NotEmpty(message = "Please Enter Your Issue")
    private String issue;

    @NotNull(message = "Please Enter Appointment Date")
    @JsonFormat(pattern = "dd/MM/yyyy", shape = JsonFormat.Shape.STRING)
    @FutureOrPresent(message = "Please Enter Today's Date or Future Date")
    private LocalDate date;

    @NotEmpty(message = "Please Enter Patient Name")
    private String patientName;
}
