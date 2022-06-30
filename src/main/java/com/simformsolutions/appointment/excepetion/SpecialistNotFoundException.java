package com.simformsolutions.appointment.excepetion;

import java.io.Serial;

public class SpecialistNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public SpecialistNotFoundException(String message) {
        super(message);
    }
}
