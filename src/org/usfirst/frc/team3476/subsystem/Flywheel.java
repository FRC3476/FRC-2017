package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.utility.LoadController;
import org.usfirst.frc.team3476.utility.Threaded;

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
	private double batVolt;


	private DigitalInput ballSensor;

	public Flywheel(int masterTalonId, int slaveTalonId, int ballSensorId) {
		masterTalon = new CANTalon(masterTalonId);
		masterTalon.changeControlMode(TalonControlMode.Speed);
		slaveTalon = new CANTalon(slaveTalonId);
		slaveTalon.changeControlMode(TalonControlMode.Follower);
		slaveTalon.set(masterTalonId);

		masterTalon.setStatusFrameRateMs(StatusFrameRate.QuadEncoder, 10);
		masterTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		masterTalon.configEncoderCodesPerRev(1024);
		//masterTalon.configPeakOutputVoltage(0, -12);
		
		masterTalon.enableBrakeMode(false);
		slaveTalon.enableBrakeMode(false);

		masterTalon.reverseOutput(true);
		masterTalon.reverseSensor(false);
		slaveTalon.reverseOutput(true);

		masterTalon.clearStickyFaults();
		slaveTalon.clearStickyFaults();
		
		//CTRE advice for tuning is to increase period until it is NOT as granular 
		//then increase the rolling average until it is smooth enough but still responsive
		
		// TODO: Voltage Compensation (Probably change feedforward)
		batVolt = DriverStation.getInstance().getBatteryVoltage();

		ballSensor = new DigitalInput(ballSensorId);

	}

	public void setSetpoint(double setpoint) {
		this.setpoint = setpoint;
		masterTalon.setSetpoint(setpoint);
	}

	public void setTolerance(double toleranceRange) {
		this.toleranceRange = toleranceRange;
	}

	public boolean isDone() {
		return (Math.abs(setpoint - masterTalon.getSpeed()) < toleranceRange);
	}

	public double getSpeed() {
		return masterTalon.getSpeed();
	}

	public double getSetpoint() {
		return setpoint;
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
	
	public boolean get()
	{
		return ballSensor.get();
	}

}
