package com.simformsolutions.appointment.excepetion;

import java.io.Serial;

public class StatusChangeException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public StatusChangeException(String message) {
        super(message);
    }
}
