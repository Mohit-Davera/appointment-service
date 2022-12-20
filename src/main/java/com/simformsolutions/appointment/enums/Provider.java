package com.simformsolutions.appointment.enums;

public enum Provider {
    LOCAL("local"), GOOGLE("google"), KEYCLOAK("keycloak");

    public final String label;

    Provider(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }
}