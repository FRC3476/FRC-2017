package org.usfirst.frc.team3476.utility;

public class InterpolableValue<D extends Interpolable<D>> {

	private D value;
	private double key;

	public InterpolableValue(double key, D value) {
		this.key = key;
		this.value = value;
	}

	public double getKey() {
		return key;
	}

	public D getValue() {
		return value;
	}
}
