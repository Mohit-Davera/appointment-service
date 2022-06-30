package com.simformsolutions.appointment.excepetion;

import java.io.Serial;

public class DoctorNotAvailableException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public DoctorNotAvailableException(String errorMessage) {
        super(errorMessage);
    }
}
