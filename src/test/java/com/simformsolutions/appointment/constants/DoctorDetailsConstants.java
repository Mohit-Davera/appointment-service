package com.simformsolutions.appointment.constants;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public final class DoctorDetailsConstants {
    public static final int DOCTOR_ID = 1;
    public static final String FIRST_NAME = "Hansraj";
    public static final String LAST_NAME = "Hathi";
    public static final String PHONE_NUMBER = "9409598787";
    public static final String EMAIL = "hathi@gmail.com";
    public static final String CITY = "Rajkot";
    public static final String DEGREE = "MBBS";
    public static final String COLLEGE_NAME = "GEC";
    public static final int EXPERIENCE = 3;
    public static final LocalTime ENTRY_TIME = LocalTime.parse("10:00", DateTimeFormatter.ofPattern("HH:mm"));
    public static final LocalTime EXIT_TIME = LocalTime.parse("21:00", DateTimeFormatter.ofPattern("HH:mm"));

}
