package com.simformsolutions.appointment.enums;

public enum AppointmentStatus {
    BOOKED("Booked"),
    CANCELLED("Cancelled"),
    AVAILABLE("Available"),
    RESCHEDULE("Reschedule");
    public final String label;

    AppointmentStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }
}
