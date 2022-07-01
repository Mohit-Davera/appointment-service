package com.simformsolutions.appointment.constants;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public final class AppointmentDoctorDetailsConstants {
    public static final int APPOINTMENT_ID1 = 1;
    public static final int APPOINTMENT_ID2 = 2;
    public static final String DOCTOR_NAME = "Hansraj Hathi";
    public static final LocalTime BOOKING_TIME = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
    public static final LocalDate BOOKING_DATE = LocalDate.now();
    public static final String BOOKED_STATUS = "BOOKED";

}
