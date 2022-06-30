package com.simformsolutions.appointment.excepetion;

import java.io.Serial;

public class DoctorNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public DoctorNotFoundException(String message) {
        super(message);
    }
}
