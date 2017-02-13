package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.utility.Constants;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.StatusFrameRate;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.DriverStation;

public class Flywheels {

	private CANTalon leftMasterTalon, leftSlaveTalon;
	private CANTalon rightMasterTalon, rightSlaveTalon;
	
	private double leftSetpoint, rightSetpoint;
	
	private double toleranceRange = 50;
	private double batVolt;

	private static Flywheels flywheelsInstance = new Flywheels();
	
	public static Flywheels getInstance() {
		return flywheelsInstance;
	}
	
	private Flywheels() {
		leftMasterTalon = new CANTalon(Constants.LeftMasterFlywheelId, 1);
		leftMasterTalon.changeControlMode(TalonControlMode.Speed);
		leftSlaveTalon = new CANTalon(Constants.LeftSlaveFlywheelId);
		leftSlaveTalon.changeControlMode(TalonControlMode.Follower);
		leftSlaveTalon.set(Constants.LeftMasterFlywheelId);

		leftMasterTalon.enableBrakeMode(false);
		leftSlaveTalon.enableBrakeMode(false);
		
		leftMasterTalon.reverseOutput(false);
		leftMasterTalon.reverseSensor(true);
		leftSlaveTalon.reverseOutput(true);

		leftMasterTalon.clearStickyFaults();
		leftSlaveTalon.clearStickyFaults();
		
		leftMasterTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		leftMasterTalon.configEncoderCodesPerRev(1024);
		leftMasterTalon.configPeakOutputVoltage(12, 0);
		leftMasterTalon.setStatusFrameRateMs(StatusFrameRate.QuadEncoder, 10);

		// TODO: Voltage Compensation (Probably change feedforward)
		batVolt = DriverStation.getInstance().getBatteryVoltage();

		/* leftMasterTalon.setP(0.28);
		 * leftMasterTalon.setI(0);
		 * leftMasterTalon.setD(7);
		 * leftMasterTalon.setF(0.0125);
		 * Constants TBD */

		rightMasterTalon = new CANTalon(Constants.RightMasterFlywheelId);
		rightMasterTalon.changeControlMode(TalonControlMode.Speed);
		rightSlaveTalon = new CANTalon(Constants.RightSlaveFlywheelId);
		rightSlaveTalon.changeControlMode(TalonControlMode.Follower);
		rightSlaveTalon.set(Constants.RightMasterFlywheelId);

		rightMasterTalon.enableBrakeMode(false);
		rightSlaveTalon.enableBrakeMode(false);
		
		rightMasterTalon.reverseOutput(false);
		rightMasterTalon.reverseSensor(false);
		rightSlaveTalon.reverseOutput(false);
		
		rightMasterTalon.clearStickyFaults();
		rightSlaveTalon.clearStickyFaults();

		rightMasterTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		rightMasterTalon.configEncoderCodesPerRev(1024);
		rightMasterTalon.configPeakOutputVoltage(12, 0);
		rightMasterTalon.setStatusFrameRateMs(StatusFrameRate.QuadEncoder, 10);
	}

	public void setLeftSetpoint(double setpoint) {
		this.leftSetpoint = setpoint;
		leftMasterTalon.setSetpoint(setpoint);
	}

	public void setRightSetpoint(double setpoint) {
		this.rightSetpoint = setpoint;
		rightMasterTalon.setSetpoint(setpoint);
	}
	public void setTolerance(double toleranceRange) {
		this.toleranceRange = toleranceRange;
	}

	public boolean isDone() {
		return (Math.abs(leftSetpoint - leftMasterTalon.getSpeed()) < toleranceRange) && (Math.abs(rightSetpoint - rightMasterTalon.getSpeed()) < toleranceRange);
	}

	public double getLeftSpeed() {
		return leftMasterTalon.getSpeed();
	}
	
	public double getRightSpeed() {
		return rightMasterTalon.getSpeed();
	}

	public double getLeftSetpoint() {
		return leftSetpoint;
	}
	
	public double getRightSetpoint() {
		return rightSetpoint;
	}
	
	public void leftEnable()
	{
		leftMasterTalon.enable();
	}
	public void leftDisable()
	{
		leftMasterTalon.disable();
	}

}
