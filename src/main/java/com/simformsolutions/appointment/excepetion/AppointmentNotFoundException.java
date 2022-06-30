package com.simformsolutions.appointment.excepetion;

import java.io.Serial;

public class AppointmentNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public AppointmentNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
