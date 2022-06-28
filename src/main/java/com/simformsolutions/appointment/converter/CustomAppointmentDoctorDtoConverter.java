package com.simformsolutions.appointment.converter;

import org.springframework.core.convert.converter.Converter;
import com.simformsolutions.appointment.dto.AppointmentDoctorDto;
import org.springframework.stereotype.Component;

import javax.persistence.Tuple;
import java.sql.Date;
import java.sql.Time;

@Component
public class CustomAppointmentDoctorDtoConverter implements Converter<Tuple, AppointmentDoctorDto> {
    @Override
    public AppointmentDoctorDto convert(Tuple t) {
        return new AppointmentDoctorDto(
                        t.get(0, Integer.class),
                        t.get(1, Integer.class),
                        t.get(2, String.class) + " " + t.get(3, String.class),
                        t.get(4, Integer.class),
                        t.get(5, String.class),
                        t.get(6, Time.class).toLocalTime(),
                        t.get(7, Date.class).toLocalDate(),
                        t.get(8, String.class)
                );
    }
}
