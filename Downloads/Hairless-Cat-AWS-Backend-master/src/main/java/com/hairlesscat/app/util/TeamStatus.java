package com.hairlesscat.app.util;

public enum TeamStatus {
	PENDING(0),
	CONFIRMED(1),
	CANCELLED(2);

	private final int value;

	TeamStatus(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}
}
