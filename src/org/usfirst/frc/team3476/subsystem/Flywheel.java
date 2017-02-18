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

public class Flywheel extends Threaded {

	private CANTalon masterTalon, slaveTalon;

	private double setpoint;
	private double toleranceRange = 50;
	private double batVolt;

	private boolean isEnabled;
	// public for testing until Constants are found
	public LoadController loadCompensator;

	private DigitalInput ballSensor;

	public Flywheel(int masterTalonId, int slaveTalonId, int ballSensorId) {
		// temporary? fast update rate
		RUNNINGSPEED = 1;
		masterTalon = new CANTalon(masterTalonId, 1);
		masterTalon.changeControlMode(TalonControlMode.PercentVbus);
		slaveTalon = new CANTalon(slaveTalonId);
		slaveTalon.changeControlMode(TalonControlMode.Follower);
		slaveTalon.set(masterTalonId);

		masterTalon.setStatusFrameRateMs(StatusFrameRate.QuadEncoder, 10);

		masterTalon.enableBrakeMode(false);
		slaveTalon.enableBrakeMode(false);

		masterTalon.reverseOutput(false);
		masterTalon.reverseSensor(true);
		slaveTalon.reverseOutput(true);

		masterTalon.clearStickyFaults();
		slaveTalon.clearStickyFaults();

		masterTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		masterTalon.configEncoderCodesPerRev(1024);
		masterTalon.configPeakOutputVoltage(12, 0);
		//masterTalon.setStatusFrameRateMs(StatusFrameRate.QuadEncoder, 10);
		
		//CTRE advice for tuning is to increase period until it is NOT as granular 
		//then increase the rolling average until it is smooth enough but still responsive
		masterTalon.SetVelocityMeasurementPeriod(VelocityMeasurementPeriod.Period_10Ms);
		masterTalon.SetVelocityMeasurementWindow(64);
		
		// TODO: Voltage Compensation (Probably change feedforward)
		batVolt = DriverStation.getInstance().getBatteryVoltage();

		ballSensor = new DigitalInput(ballSensorId);

		loadCompensator = new LoadController(0.00025, 2, 0.05);
	}

	@Override
	public void update() {
		if (isEnabled) {
			masterTalon.set(loadCompensator.calculate(ballSensor.get(), setpoint));
		}
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
		isEnabled = true;
	}

	public void disable() {
		masterTalon.disable();
		isEnabled = false;
	}

	public double getCurrent() {
		return masterTalon.getOutputCurrent();
	}

}
