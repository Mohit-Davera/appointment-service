package com.simformsolutions.appointment.enums;

public enum Role {
	USER("user"), DOCTOR("doctor"),ADMIN("admin");

	public final String label;

	Role(String label) {
		this.label = label;
	}

	public String getLabel() {
		return this.label;
	}
}
