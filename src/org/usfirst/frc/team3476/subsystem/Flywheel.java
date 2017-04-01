package org.usfirst.frc.team3476.subsystem;


import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.StatusFrameRate;
import com.ctre.CANTalon.TalonControlMode;
import com.ctre.CANTalon.VelocityMeasurementPeriod;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;

public class Flywheel {

	private CANTalon masterTalon, slaveTalon;

	private double setpoint;
	private double toleranceRange = 50;

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
		masterTalon.reverseSensor(false);
		slaveTalon.reverseOutput(true);

		masterTalon.clearStickyFaults();
		slaveTalon.clearStickyFaults();
		
		masterTalon.SetVelocityMeasurementPeriod(VelocityMeasurementPeriod.Period_100Ms);
		masterTalon.SetVelocityMeasurementWindow(64);
		masterTalon.setNominalClosedLoopVoltage(12);
		masterTalon.setPID(0.01, 0, 0, 0.0278, 0, 0, 0);
		
		masterTalon.configPeakOutputVoltage(12, 0);
	}

	public void setSetpoint(double setpoint) {
		masterTalon.changeControlMode(TalonControlMode.Speed);
		masterTalon.setSetpoint(setpoint);
	}

	public synchronized void setTolerance(double toleranceRange) {
		this.toleranceRange = toleranceRange;
	}

	public boolean isDone() {
		return (Math.abs(setpoint - masterTalon.getSpeed()) < toleranceRange);
	}

	public double getSpeed() {
		return masterTalon.getSpeed();
	}

	public double getSetpoint() {
		return masterTalon.getSetpoint();
	}

	public void enable() {
		masterTalon.enable();
	}

	public void disable() {
		masterTalon.disable();
	}

	public double getCurrent() {
		return masterTalon.getOutputCurrent();
	}
	
	public void setPIDF(double P, double I, double D, double F){
		masterTalon.setPID(P, I, D);
		masterTalon.setF(F);
	}

	public void setVelocityMeasurementPeriod(VelocityMeasurementPeriod periodMs){
		masterTalon.SetVelocityMeasurementPeriod(periodMs);
	}

	public void setVelocityMeasurementWindow(int periodMs){
		masterTalon.SetVelocityMeasurementWindow(periodMs);
	}
	
	public void setPercent(double percent){
		masterTalon.changeControlMode(TalonControlMode.PercentVbus);
		masterTalon.set(percent);
	}
	public void setVoltage(double voltage){
		masterTalon.changeControlMode(TalonControlMode.Voltage);
		masterTalon.set(voltage);
	}
	
	public double getOutputVoltage(){
		return masterTalon.getOutputVoltage();
	}
	
	public void config()
	{
		masterTalon.enableBrakeMode(false);
		slaveTalon.enableBrakeMode(false);
		masterTalon.configPeakOutputVoltage(0, -12);
	}

}
