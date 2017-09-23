package org.usfirst.frc.team3476.utility;

public class Translation2d implements Interpolable {

	public static Translation2d fromAngleDistance(double distance, Rotation angle) {
		return new Translation2d(angle.sin() * distance, angle.cos() * distance);
	}

	private double x;

	private double y;

	public Translation2d() {
		x = 0;
		y = 0;
	}

	public Translation2d(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Rotation getAngleFromOffset(Translation2d offset) {
		return offset.getAngleTo(this);
	}

	public Rotation getAngleTo(Translation2d nextPoint) {
		double angleOffset = Math.asin((x - nextPoint.getX()) / getDistanceTo(nextPoint));
		return Rotation.fromRadians(angleOffset);
	}

	public double getDistanceTo(Translation2d nextPoint) {
		return Math.sqrt(Math.pow((x - nextPoint.getX()), 2) + Math.pow(y - nextPoint.getY(), 2));
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public Translation2d inverse() {
		return new Translation2d(-x, -y);
	}

	public Translation2d rotateBy(Rotation rotationMat) {
		x = x * rotationMat.cos() - y * rotationMat.sin();
		y = x * rotationMat.sin() + y * rotationMat.cos();
		return new Translation2d(x, y);
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public Translation2d translateBy(Translation2d delta) {

		return new Translation2d(x + delta.getX(), y + delta.getY());
	}

	@Override
	public Interpolable interpolate(Interpolable other, double percentage) {
		return null;
	}
}
