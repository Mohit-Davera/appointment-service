package com.simformsolutions.doctorappointmentsystem.Projection;

import java.time.LocalTime;

public interface DoctorInter {

    int getDoctorId();
    LocalTime getEntryTime();
    LocalTime getExitTime();
    LocalTime getExperience();
}
