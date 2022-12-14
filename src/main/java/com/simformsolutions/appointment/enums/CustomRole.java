package com.simformsolutions.appointment.enums;

public enum CustomRole {
    USER("USER"), DOCTOR("DOCTOR"),ADMIN("ADMIN");

    public final String label;

    CustomRole(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }
}