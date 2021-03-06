package org.usfirst.frc.team3476.utility;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.StatusFrameRate;
import com.ctre.CANTalon.TalonControlMode;
import com.ctre.CANTalon.VelocityMeasurementPeriod;

public class Flywheel {

	private CANTalon masterTalon, slaveTalon;

	private double toleranceRange = 200;

	public Flywheel(int masterTalonId, int slaveTalonId) {
		masterTalon = new CANTalon(masterTalonId);
		masterTalon.changeControlMode(TalonControlMode.Speed);
		slaveTalon = new CANTalon(slaveTalonId);
		slaveTalon.changeControlMode(TalonControlMode.Follower);
		slaveTalon.set(masterTalonId);

		masterTalon.setStatusFrameRateMs(StatusFrameRate.QuadEncoder, 10);
		masterTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		masterTalon.configEncoderCodesPerRev(1024);

		masterTalon.enableBrakeMode(false);
		slaveTalon.enableBrakeMode(false);

		masterTalon.reverseOutput(false);
		masterTalon.reverseSensor(true);
		slaveTalon.reverseOutput(true);

		masterTalon.clearStickyFaults();
		slaveTalon.clearStickyFaults();

		masterTalon.SetVelocityMeasurementPeriod(VelocityMeasurementPeriod.Period_100Ms);
		masterTalon.SetVelocityMeasurementWindow(64);
		masterTalon.setNominalClosedLoopVoltage(12);
		masterTalon.setPID(0.0012, 0, 0, 0.0240, 0, 0, 0);

		masterTalon.configPeakOutputVoltage(12, 0);
	}

	public void config() {
		masterTalon.enableBrakeMode(false);
		slaveTalon.enableBrakeMode(false);
		masterTalon.configPeakOutputVoltage(0, -12);
	}

	public void disable() {
		masterTalon.disable();
	}

	public void enable() {
		masterTalon.enable();
	}

	public double getCurrent() {
		return masterTalon.getOutputCurrent();
	}

	public double getOutputVoltage() {
		return masterTalon.getOutputVoltage();
	}

	public double getSetpoint() {
		return masterTalon.getSetpoint();
	}

	public double getSpeed() {
		return masterTalon.getSpeed();
	}

	public boolean isDone() {
		return (Math.abs(masterTalon.getSetpoint() - masterTalon.getSpeed()) < toleranceRange);
	}

	public void setPercent(double percent) {
		masterTalon.changeControlMode(TalonControlMode.PercentVbus);
		masterTalon.set(percent);
	}

	public void setPIDF(double P, double I, double D, double F) {
		masterTalon.setPID(P, I, D);
		masterTalon.setF(F);
	}

	public void setSetpoint(double setpoint) {
		masterTalon.changeControlMode(TalonControlMode.Speed);
		masterTalon.setSetpoint(setpoint);
	}

	public synchronized void setTolerance(double toleranceRange) {
		this.toleranceRange = toleranceRange;
	}

	public void setVelocityMeasurementPeriod(VelocityMeasurementPeriod periodMs) {
		masterTalon.SetVelocityMeasurementPeriod(periodMs);
	}

	public void setVelocityMeasurementWindow(int periodMs) {
		masterTalon.SetVelocityMeasurementWindow(periodMs);
	}

	public void setVoltage(double voltage) {
		masterTalon.changeControlMode(TalonControlMode.Voltage);
		masterTalon.set(voltage);
	}

}
