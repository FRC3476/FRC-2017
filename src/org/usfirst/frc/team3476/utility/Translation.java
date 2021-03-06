package org.usfirst.frc.team3476.utility;

public class Translation implements Interpolable {

	public static Translation fromAngleDistance(double distance, Rotation angle) {
		return new Translation(angle.sin() * distance, angle.cos() * distance);
	}

	private double x;

	private double y;

	public Translation() {
		x = 0;
		y = 0;
	}

	public Translation(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Rotation getAngleFromOffset(Translation offset) {
		return offset.getAngleTo(this);
	}

	public Rotation getAngleTo(Translation nextPoint) {
		double angleOffset = Math.asin((x - nextPoint.getX()) / getDistanceTo(nextPoint));
		return Rotation.fromRadians(angleOffset);
	}

	public double getDistanceTo(Translation nextPoint) {
		return Math.sqrt(Math.pow((x - nextPoint.getX()), 2) + Math.pow(y - nextPoint.getY(), 2));
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public Translation inverse() {
		return new Translation(-x, -y);
	}

	public Translation rotateBy(Rotation rotationMat) {
		x = x * rotationMat.cos() - y * rotationMat.sin();
		y = x * rotationMat.sin() + y * rotationMat.cos();
		return new Translation(x, y);
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public Translation translateBy(Translation delta) {

		return new Translation(x + delta.getX(), y + delta.getY());
	}

	@Override
	public Interpolable interpolate(Interpolable other, double percentage) {
		return null;
	}
}
