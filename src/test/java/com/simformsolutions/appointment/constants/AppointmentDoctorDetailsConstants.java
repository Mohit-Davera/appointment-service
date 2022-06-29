package com.simformsolutions.appointment.constants;

import org.apache.tomcat.jni.Local;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public final class AppointmentDoctorDetailsConstants {
    public static final int APPOINTMENT_ID = 1;
    public static final int APPOINTMENT_ID2 = 2;
    public static final String DOCTOR_NAME = "Hansraj Hathi";
    public static final LocalTime BOOKING_TIME = LocalTime.parse("10:00", DateTimeFormatter.ofPattern("HH:mm"));
    public static final LocalDate BOOKING_DATE = LocalDate.now();
    public static final String BOOKED_STATUS = "BOOKED";

    public static final String AVAILABLE_STATUS = "AVAILABLE";


}
