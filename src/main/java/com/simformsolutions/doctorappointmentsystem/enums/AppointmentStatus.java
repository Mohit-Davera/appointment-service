package com.simformsolutions.doctorappointmentsystem.enums;


public enum AppointmentStatus {
    BOOKED("Booked"),
    CANCELLED("Cancelled"),
    AVAILABLE("Available"),
    RESCHDULE("Reschedule");
    public final String label;
    AppointmentStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }
}
