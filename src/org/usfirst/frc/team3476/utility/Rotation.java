package org.usfirst.frc.team3476.utility;

public class Rotation implements Interpolable<Rotation> {

	public static Rotation fromDegrees(double angle) {
		return Rotation.fromRadians(Math.toRadians(angle));
	}

	public static Rotation fromRadians(double radians) {
		return new Rotation(Math.cos(radians), Math.sin(radians));
	}

	private double cos;

	private double sin;

	public Rotation() {
		cos = 1;
		sin = 0;
	}

	public Rotation(double cos, double sin) {
		this.cos = cos;
		this.sin = sin;
	}

	public Rotation(double cos, double sin, boolean normalize) {
		this.cos = cos;
		this.sin = sin;
		if (normalize) {
			normalize();
		}
	}

	public double cos() {
		return cos;
	}

	public double getDegrees() {
		return Math.toDegrees(getRadians());
	}

	public double getRadians() {
		return Math.atan2(sin, cos);
	}

	@Override
	public Rotation interpolate(Rotation other, double percentage) {
		Rotation diff = inverse().rotateBy(other);
		return rotateBy(Rotation.fromRadians(diff.getRadians() * percentage));
	}

	public Rotation inverse() {
		return new Rotation(cos, -sin);
	}

	public void normalize() {
		double magnitude = Math.hypot(cos, sin);
		if (magnitude > 1E-9) {
			cos /= magnitude;
			sin /= magnitude;
		} else {
			cos = 1;
			sin = 0;
		}
	}

	public Rotation rotateBy(Rotation rotationMat) {
		return new Rotation(cos * rotationMat.cos() - sin * rotationMat.sin(),
				sin * rotationMat.cos() + cos * rotationMat.sin(), true);
	}

	public double sin() {
		return sin;
	}
}