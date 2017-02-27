package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.utility.ADXRS453_Gyro;
import org.usfirst.frc.team3476.utility.Constants;
import org.usfirst.frc.team3476.utility.Dashcomm;
import org.usfirst.frc.team3476.utility.Path;
import org.usfirst.frc.team3476.utility.PurePursuitController;
import org.usfirst.frc.team3476.utility.Rotation;
import org.usfirst.frc.team3476.utility.SynchronousPid;
import org.usfirst.frc.team3476.utility.Threaded;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.MotionProfileStatus;
import com.ctre.CANTalon.StatusFrameRate;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SPI.Port;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

/* Inspiration from Team 254 */

public class OrangeDrive extends Threaded {
	public enum DriveState {
		MANUAL, AUTO, GEAR
	}

	private DriveState driveState = DriveState.MANUAL;

	private double desiredAngle;

	private boolean isDone;

	//private RobotDrive driveBase;
	private ADXRS453_Gyro gyroSensor;
	private CANTalon leftTalon, rightTalon;
	private PurePursuitController autonomousDriver;
	private SynchronousPid gearDriver = new SynchronousPid(0.1, 0.01, 0, 0.1);
	private DriveVelocity autoDriveVelocity;
	private static final OrangeDrive driveInstance = new OrangeDrive();
	private Solenoid driveShifters = new Solenoid(Constants.ShifterSolenoidId);
	private double gearDeltaSpeed = 0;
	//private RobotDrive driveBase;
	// Do not do private static double MINIMUM_INPUT = Constants.MinimumControllerInput;
	// just use Constants.X
	
	
	public static OrangeDrive getInstance() {
		return driveInstance;
	}

	private OrangeDrive() {
		RUNNINGSPEED = 10;
		
		leftTalon = new CANTalon(Constants.LeftMasterDriveId);
		rightTalon = new CANTalon(Constants.RightMasterDriveId);

		leftTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		rightTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		
		// Quadrature updates at 100ms

		leftTalon.setStatusFrameRateMs(StatusFrameRate.QuadEncoder, 10);
		rightTalon.setStatusFrameRateMs(StatusFrameRate.QuadEncoder, 10);

		leftTalon.configEncoderCodesPerRev(1024);
		rightTalon.configEncoderCodesPerRev(1024);

		leftTalon.reverseOutput(false);
		leftTalon.reverseSensor(true);
		rightTalon.reverseOutput(true);
		rightTalon.reverseSensor(false);

		CANTalon leftSlaveTalon = new CANTalon(Constants.LeftSlaveDriveId);
		CANTalon rightSlaveTalon = new CANTalon(Constants.RightSlaveDriveId);

		leftSlaveTalon.changeControlMode(TalonControlMode.Follower);
		leftSlaveTalon.set(leftTalon.getDeviceID());
		rightSlaveTalon.changeControlMode(TalonControlMode.Follower);
		rightSlaveTalon.set(rightTalon.getDeviceID());
		
		gyroSensor = new ADXRS453_Gyro(SPI.Port.kOnboardCS0);
		configureTalons(TalonControlMode.Speed);
	}
	
	@Override
	public synchronized void update() {
		switch (driveState) {
		case MANUAL:
			break;
		case AUTO:
			updateAutoPath();
			break;
		case GEAR:
			updateGearPath();
			break;
		}
	}

	public synchronized void setManualDrive(double moveValue, double turnValue) {
		// low2 + (value - low1) * (high2 - low2) / (high1 - low1)
		if(driveState != DriveState.MANUAL){
			driveState = DriveState.MANUAL;
		}
		if (Math.abs(moveValue) >= Constants.MinimumControllerInput) {
			moveValue = (moveValue * (Math.abs(moveValue) - Constants.MinimumControllerInput)) / ((0.85) * Math.abs(moveValue));
			// 0.7 = Constants.MaximumControllerInput - Constants.MinimumControllerInput
		} else {
			moveValue = 0;
		}

		if (Math.abs(turnValue) >= Constants.MinimumControllerInput) {
			turnValue = turnValue * (Math.abs(turnValue) - Constants.MinimumControllerInput)
					/ (Constants.MaximumControllerInput - Constants.MinimumControllerInput) * Math.abs(turnValue);
		} else {
			turnValue = 0;
		}
		arcadeDrive(moveValue, turnValue);
	}

	public synchronized void setAutoPath(Path autoPath) {
		//robotState = RobotTracker.getInstance();
		// PurePursuitController(double lookAheadDistance, double robotSpeed,
		// double robotDiameter, Path robotPath)
		if(driveState != DriveState.AUTO){
			driveState = DriveState.AUTO;
		}
		autonomousDriver = new PurePursuitController(10, 10, 25.5, autoPath);
		shiftUp();
		updateAutoPath();
	}

	public synchronized void setGearPath() {
		if(driveState != DriveState.GEAR){
			driveState = DriveState.GEAR;
			isDone = false;
			desiredAngle = gyroSensor.getAngle() + Dashcomm.get("angle", 0);
			gearDeltaSpeed = 0;
			gearDriver.setSetpoint(desiredAngle);
			updateGearPath();
		}
	}

	private synchronized void setWheelVelocity(DriveVelocity setVelocity) {
		// inches per sec to rotations per min
		if(setVelocity.wheelSpeed > 216){
			DriverStation.getInstance();
			DriverStation.reportError("Velocity set over 216!", false);
			return;
		}
		
		leftTalon.setSetpoint((setVelocity.wheelSpeed + setVelocity.deltaSpeed) * 15);
		rightTalon.setSetpoint((setVelocity.wheelSpeed - setVelocity.deltaSpeed) * 15);		
	}

	public boolean isDone() {
		switch (driveState) {
		case MANUAL:
			return true;
		case AUTO:
			return autonomousDriver.isDone(RobotTracker.getInstance().getCurrentPosition());
		case GEAR:
			return isDone;
		}
		return true;
	}

	private void updateAutoPath() {
		autoDriveVelocity = autonomousDriver.calculate(RobotTracker.getInstance().getCurrentPosition());
		setWheelVelocity(autoDriveVelocity);
	}

	private void updateGearPath() {
		if (Math.abs(desiredAngle - gyroSensor.getAngle()) > Constants.GearAngleTolerance) {
			if(desiredAngle - gyroSensor.getAngle() < 0){
				setWheelVelocity(new DriveVelocity(0, -20));
			} else {
				setWheelVelocity(new DriveVelocity(0, 20));
			}
			System.out.println("turning " + gyroSensor.getAngle());
			System.out.println("desired " + desiredAngle);
		} else {
			System.out.println("driving");
			if(isDone){
				DriveVelocity drivingSpeed = new DriveVelocity(10, 0);
				setWheelVelocity(drivingSpeed);
				System.out.println("isPushed");
			} else {
				/*
				if(Dashcomm.get("isNew", 0) == 1){
					desiredAngle = Dashcomm.get("angle", 0) + gyroSensor.getAngle();	
					Dashcomm.put("isNew", 0);
				} 
				gearDeltaSpeed = (desiredAngle - gyroSensor.getAngle() * 0.25);
				System.out.println(desiredAngle - gyroSensor.getAngle());
				*/
				
				DriveVelocity drivingSpeed = new DriveVelocity(-10, 0);
				setWheelVelocity(drivingSpeed);
				if(Gear.getInstance().isPushed()){					
					isDone = true;
				}
			}
		}
	}

	private void configureTalons(TalonControlMode mode) {
		leftTalon.changeControlMode(mode);
		rightTalon.changeControlMode(mode);
	}

	// TODO: Return wheel in inches or something
	public double getLeftDistance() {
		return leftTalon.getPosition() * Constants.WheelDiameter;
	}

	public double getRightDistance() {
		return rightTalon.getPosition() * Constants.WheelDiameter;
	}

	public Rotation getGyroAngle(){
		return new Rotation(gyroSensor.getAngle());
	}
	
	public double getDegreeAngle(){
		return gyroSensor.getAngle();
	}
	
	private void arcadeDrive(double moveValue, double rotateValue) {

		double leftMotorSpeed;
		double rightMotorSpeed;

		if (moveValue >= 0.0) {
			moveValue = moveValue * moveValue;
		} else {
			moveValue = -(moveValue * moveValue);
		}
		if (rotateValue >= 0.0) {
			rotateValue = rotateValue * rotateValue;
		} else {
			rotateValue = -(rotateValue * rotateValue);
		}

		if (moveValue > 0.0) {
			if (rotateValue > 0.0) {
				leftMotorSpeed = moveValue - rotateValue;
				rightMotorSpeed = Math.max(moveValue, rotateValue);
			} else {
				leftMotorSpeed = Math.max(moveValue, -rotateValue);
				rightMotorSpeed = moveValue + rotateValue;
			}
		} else {
			if (rotateValue > 0.0) {
				leftMotorSpeed = -Math.max(-moveValue, rotateValue);
				rightMotorSpeed = moveValue + rotateValue;
			} else {
				leftMotorSpeed = moveValue - rotateValue;
				rightMotorSpeed = -Math.max(-moveValue, -rotateValue);
			}
		}

		// 18 ft per sec -> 216 inches per sec
		leftMotorSpeed *= 25;
		rightMotorSpeed *= 25;

		setWheelVelocity(new DriveVelocity((leftMotorSpeed + rightMotorSpeed) / 2, (leftMotorSpeed - rightMotorSpeed) / 2));
	}
	
	/*
	public void drive(double moveValue, double turnValue)
	{
		if (Math.abs(moveValue) >= Constants.MinimumControllerInput) {
			moveValue = (moveValue * (Math.abs(moveValue) - 0.3)) / ((0.7) * Math.abs(moveValue));
		} else {
			moveValue = 0;
		}

		if (Math.abs(turnValue) >= Constants.MinimumControllerInput) {
			turnValue = turnValue * (Math.abs(turnValue) - Constants.MinimumControllerInput)
					/ (Constants.MaximumControllerInput - Constants.MinimumControllerInput) * Math.abs(turnValue);
		} else {
			turnValue = 0;
		}
		
		driveBase.setSafetyEnabled(false);
		driveBase.arcadeDrive(moveValue, turnValue);
		
	}
	*/	 
	
	public void shiftDown(){
		driveShifters.set(false);
	}
	public void shiftUp(){
		driveShifters.set(true);
	}
	
	public void zeroSensors(){
		gyroSensor.calibrate();
		gyroSensor.reset();
		leftTalon.setPosition(0);
		rightTalon.setPosition(0);
	}
	
	public static class DriveVelocity {

		public double wheelSpeed;
		public double deltaSpeed;

		public DriveVelocity(double wheelSpeed, double deltaSpeed) {
			this.wheelSpeed = wheelSpeed;
			this.deltaSpeed = deltaSpeed;
		}

	}
}
