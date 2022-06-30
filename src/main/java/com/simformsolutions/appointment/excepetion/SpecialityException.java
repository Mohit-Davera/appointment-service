package com.simformsolutions.appointment.excepetion;

import java.io.Serial;

public class SpecialityException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public SpecialityException(String message) {
        super(message);
    }
}
