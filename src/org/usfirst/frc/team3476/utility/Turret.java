package org.usfirst.frc.team3476.utility;

import org.usfirst.frc.team3476.robot.Constants;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.StatusFrameRate;
import com.ctre.CANTalon.TalonControlMode;

public class Turret {

	private double tolerance;
	private CANTalon turretTalon;

	public Turret(int turretTalonId) {
		turretTalon = new CANTalon(turretTalonId);
		turretTalon.enableBrakeMode(true);
		turretTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		turretTalon.setStatusFrameRateMs(StatusFrameRate.QuadEncoder, 1);
		turretTalon.reverseSensor(true);
		turretTalon.configPeakOutputVoltage(6, -6);
		turretTalon.setPID(1.15, 0, 10);
		turretTalon.setPosition(0);
		tolerance = angleToTicks(Rotation.fromDegrees(1.5));
		turretTalon.setAllowableClosedLoopErr((int) tolerance);
	}

	public double angleToTicks(Rotation setAngle) {
		return (setAngle.getRadians() / (2 * Math.PI)) * Constants.TurretTicksPerRotations;
	}

	public Rotation getAngle() {
		return Rotation.fromRadians((turretTalon.getPosition() / Constants.TurretTicksPerRotations) * 2 * Math.PI);
	}

	public Rotation getSetAngle() {
		return Rotation.fromRadians((turretTalon.getSetpoint() / Constants.TurretTicksPerRotations) * 2 * Math.PI);
	}

	public double getSetpoint() {
		return turretTalon.getSetpoint();
	}

	public boolean isDone() {
		return Math.abs(getSetAngle().getDegrees() - getAngle().getDegrees()) < 1.5;
	}

	public void resetPosition(double degrees) {
		turretTalon.setPosition(angleToTicks(Rotation.fromDegrees(degrees)));
	}

	public void setAngle(Rotation setAngle) {
		turretTalon.changeControlMode(TalonControlMode.Position);
		if (setAngle.getDegrees() < 120 && setAngle.getDegrees() > -120) {
			turretTalon.setSetpoint(angleToTicks(setAngle));
		}
	}

	public void setManual(double power) {
		turretTalon.changeControlMode(TalonControlMode.PercentVbus);
		turretTalon.setSetpoint(power);
	}

	public void setTolerance(double tolerance) {
		turretTalon.setAllowableClosedLoopErr((int) angleToTicks(Rotation.fromDegrees(tolerance)));
	}
}
