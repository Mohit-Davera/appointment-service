package com.simformsolutions.appointment.converter;

import com.simformsolutions.appointment.dto.AppointmentDoctor;
import com.simformsolutions.appointment.model.Appointment;
import com.simformsolutions.appointment.model.Doctor;
import org.springframework.stereotype.Component;

import javax.persistence.Tuple;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class AppointmentDoctorDtoConverter {
    public List<AppointmentDoctor> tuplesToAppointmentDoctorConverter(List<Tuple> tuple) {
        return tuple.stream()
                .map(t -> new AppointmentDoctor(
                        t.get(0, Integer.class),
                        t.get(1, Integer.class),
                        t.get(2, String.class) + " " + t.get(3, String.class),
                        t.get(4, Integer.class),
                        t.get(5, String.class),
                        t.get(6, Time.class).toLocalTime(),
                        t.get(7, Date.class).toLocalDate(),
                        t.get(8, String.class)
                ))
                .toList();
    }

    public List<AppointmentDoctor> freeDoctorToBookedDoctorConverter(List<Doctor> doctors, Appointment userAppointment, LocalTime currentTime) {
        List<AppointmentDoctor> bookedDoctors = new ArrayList<>();
        doctors.forEach(
                d -> bookedDoctors.add(
                        new AppointmentDoctor(d.getDoctorId(), d.getFirstName() + " " + d.getLastName(),
                                d.getExperience(),
                                userAppointment.getSpeciality(),
                                userAppointment.getDate().getDayOfMonth() <= LocalDate.now().getDayOfMonth() ? currentTime : d.getEntryTime(),
                                userAppointment.getDate()
                        )
                ));
        return bookedDoctors;
    }
}
