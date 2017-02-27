package org.usfirst.frc.team3476.utility;

public class Rotation {

	private double cos;
	private double sin;
	private double angle;
	
	public Rotation() {
		cos = 1;
		sin = 0;
	}

	public Rotation(double cos, double sin) {
		this.cos = cos;
		this.sin = sin;
		angle = Math.acos(cos);
	}

	public Rotation(double angle) {
		cos = Math.cos(angle);
		sin = Math.sin(angle);
		this.angle = angle;
	}

	public double getDegrees() {
		return angle;
	}

	// cos , sin in unit circle
	public double getRadians() {
		return Math.atan2(sin, cos);
	}

	// Rotation matrix consists of
	// cos O -sin O
	// sin O cos O
	// R = rotation matrix
	// Rotated coordinates is R * R
	// We only need cos O and sin O because the other two can be determined
	public Rotation rotateBy(Rotation rotationMat) {
		return new Rotation(cos * rotationMat.cos - sin * rotationMat.sin, sin * rotationMat.cos
				+ cos * rotationMat.sin);
	}
	

	public Rotation inverse() {
		return new Rotation(-cos, -sin);
	}

	public double cos() {
		return cos;
	}

	public double sin() {
		return sin;
	}
}