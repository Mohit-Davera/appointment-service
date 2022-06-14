package com.simformsolutions.doctorappointmentsystem.Projection;

import com.simformsolutions.doctorappointmentsystem.model.Doctor;
import lombok.ToString;

import java.time.LocalTime;

public interface DoctorInter {

    int getDoctorId();
    LocalTime getEntryTime();
    LocalTime getExitTime();
    int getExperience();
}
