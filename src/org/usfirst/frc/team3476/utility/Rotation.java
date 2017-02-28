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

	public static Rotation fromDegrees(double angle) {
		return new Rotation(Math.cos(Math.toRadians(angle)), Math.sin(Math.toRadians(angle)));	
	}
	
	public static Rotation fromRadians(double radians) {
		return new Rotation(Math.cos(radians), Math.sin(radians));
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