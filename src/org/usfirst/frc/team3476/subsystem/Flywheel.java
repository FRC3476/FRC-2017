package org.usfirst.frc.team3476.subsystem;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.StatusFrameRate;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.DriverStation;

public class Flywheel {

	private CANTalon masterTalon, slaveTalon;
	
	private double setpoint;
	
	private double toleranceRange = 50;
	private double batVolt;

	public Flywheel(int masterTalonId, int slaveTalonId) {
		masterTalon = new CANTalon(masterTalonId, 1);
		masterTalon.changeControlMode(TalonControlMode.Speed);
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
		masterTalon.setStatusFrameRateMs(StatusFrameRate.QuadEncoder, 10);

		// TODO: Voltage Compensation (Probably change feedforward)
		batVolt = DriverStation.getInstance().getBatteryVoltage();

		/* leftMasterTalon.setP(0.28);
		 * leftMasterTalon.setI(0);
		 * leftMasterTalon.setD(7);
		 * leftMasterTalon.setF(0.0125);
		 * Constants TBD */
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
	
	public void enable()
	{
		masterTalon.enable();
	}
	public void disable()
	{
		masterTalon.disable();
	}
	public double getCurrent(){
		return masterTalon.getOutputCurrent();
	}
}
