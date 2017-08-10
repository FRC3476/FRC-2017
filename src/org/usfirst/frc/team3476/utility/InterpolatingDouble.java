package org.usfirst.frc.team3476.utility;

public class InterpolatingDouble implements Interpolable<InterpolatingDouble> {

	private double value;

	public InterpolatingDouble(double value) {
		this.value = value;
	}

	public double getValue() {
		return value;
	}

	@Override
	public InterpolatingDouble interpolate(InterpolatingDouble other, double percentage) {
		double diff = other.getValue() - value;
		return new InterpolatingDouble(value + diff * percentage);
	}

}
