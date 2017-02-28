package org.usfirst.frc.team3476.subsystem;

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
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

/* Inspiration from Team 254 */

public class OrangeDrive extends Threaded {
	public enum DriveState {
		MANUAL, AUTO, GEAR
	}
	
	private enum GearState {
		TURNING, DRIVING, REVERSING
	}

	private ADXRS450_Gyro gyroSensor = new ADXRS450_Gyro(SPI.Port.kOnboardCS0);
	private SynchronousPid gearDriver = new SynchronousPid(0.05, 0, 0, 0);
	private double desiredAngle;
	private boolean isDone;

	private DriveState driveState = DriveState.MANUAL;


	private static final OrangeDrive driveInstance = new OrangeDrive();

	private CANTalon leftTalon, rightTalon;
	private RobotTracker robotState;
	private PurePursuitController autonomousDriver;
	private DriveVelocity autoDriveVelocity;
	private Solenoid driveShifters = new Solenoid(Constants.ShifterSolenoidId);	
	
	public static OrangeDrive getInstance() {
		return driveInstance;
	}

	private OrangeDrive() {
		RUNNINGSPEED = 10;
		
		robotState = RobotTracker.getInstance();
		
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
		autonomousDriver = new PurePursuitController(10, 10, 10, autoPath);
		shiftUp();
		updateAutoPath();
	}

	public synchronized void setGearPath() {
		if(driveState != DriveState.GEAR){
			driveState = DriveState.GEAR;
			isDone = false;
			desiredAngle = gyroSensor.getAngle() + Dashcomm.get("angle", 0);
			gearDriver.setSetpoint(0);
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
		NetworkTable.getTable("").putNumber("speed", leftTalon.getSpeed());
		NetworkTable.getTable("").putNumber("setpoint", leftTalon.getSetpoint());
		
	}

	public boolean isDone() {
		switch (driveState) {
		case MANUAL:
			return true;
		case AUTO:
			return autonomousDriver.isDone(robotState.getCurrentPosition());
		case GEAR:
			return isDone;
		}
		return true;
	}

	private void updateAutoPath() {
		autoDriveVelocity = autonomousDriver.calculate(robotState.getCurrentPosition());
		setWheelVelocity(autoDriveVelocity);
	}

	private void updateGearPath() {
		if (Math.abs(desiredAngle - gyroSensor.getAngle()) > Constants.GearAngleTolerance) {
			/*
			if(desiredAngle - gyroSensor.getAngle() < 0){
			
				setWheelVelocity(new DriveVelocity(0, 30));
			} else {
				setWheelVelocity(new DriveVelocity(0, -30));				
			}
			*/
			setWheelVelocity(new DriveVelocity(0, 100 * gearDriver.update(gyroSensor.getAngle() - desiredAngle)));
			System.out.println("turning" + desiredAngle);
			System.out.println("rightnow" + gyroSensor.getAngle());
			
		} else {
			if(isDone){
				DriveVelocity drivingSpeed = new DriveVelocity(100, 0);
				setWheelVelocity(drivingSpeed);
				System.out.println("reversing");
			} else {
				DriveVelocity drivingSpeed = new DriveVelocity(-100, 0);
				setWheelVelocity(drivingSpeed);
				
				System.out.println("diving");
				
				// Use timestamp of vision 
				
				
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
		return Rotation.fromDegrees(gyroSensor.getAngle());
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
		leftMotorSpeed *= 216;
		rightMotorSpeed *= 216;
		
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
