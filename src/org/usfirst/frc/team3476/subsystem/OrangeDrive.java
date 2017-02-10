package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.utility.Dashcomm;
import org.usfirst.frc.team3476.utility.Path;
import org.usfirst.frc.team3476.utility.PurePursuitController;
import org.usfirst.frc.team3476.utility.Threaded;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.StatusFrameRate;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;

	/* Much inspiration from Team 254 */

public class OrangeDrive extends Threaded {
	public enum DriveState {
		MANUAL, AUTO, GEAR
	}
	private DriveState driveState = DriveState.MANUAL;
	
	private double desiredAngle;
	
	private boolean isDone;

	private RobotDrive driveBase;
	private AnalogGyro testGyro = new AnalogGyro(0);
	private CANTalon leftWheel, rightWheel;
	private RobotTracker robotState = RobotTracker.getInstance();
	private PurePursuitController autonomousDriver;
	private DriveVelocity autoDriveVelocity;
	private static OrangeDrive driveInstance = new OrangeDrive(4, 5, 2, 3);
	
	private double MINIMUM_INPUT = 0.3;
	private double MAXIMUM_INPUT = 1;
	private double MINIMUM_OUTPUT = 0;
	private double MAXIMUM_OUTPUT = 1;
	
	public static OrangeDrive getInstance() {
		return driveInstance;
	}

	private OrangeDrive(int frontLeftMotor, int rearLeftMotor, int frontRightMotor, int rearRightMotor) {
		RUNNINGSPEED = 10;
		leftWheel = new CANTalon(frontLeftMotor);
		rightWheel = new CANTalon(frontRightMotor);
		
		leftWheel.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		rightWheel.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		
		// no need to set up codes per rev
		// Quadrature updates at 20ms
		leftWheel.setStatusFrameRateMs(StatusFrameRate.QuadEncoder, 10);
		rightWheel.setStatusFrameRateMs(StatusFrameRate.QuadEncoder, 10);		
		
		leftWheel.configEncoderCodesPerRev(1024);
		rightWheel.configEncoderCodesPerRev(1024);

		leftWheel.reverseOutput(false);
		leftWheel.reverseSensor(true);
		rightWheel.reverseOutput(true);
		rightWheel.reverseSensor(false);
		
		CANTalon leftSlaveWheel = new CANTalon(rearLeftMotor);
		CANTalon rightSlaveWheel = new CANTalon(rearRightMotor);
		
		leftSlaveWheel.changeControlMode(TalonControlMode.Follower);
		leftSlaveWheel.set(frontLeftMotor);
		rightSlaveWheel.changeControlMode(TalonControlMode.Follower);
		rightSlaveWheel.set(frontRightMotor);
		
		
		// drive code default is reversed as it assumes there is one reversal
		configureTalons(TalonControlMode.Speed);
		driveBase = new RobotDrive(leftWheel, rightWheel);
		//driveBase.setInvertedMotor(MotorType.kRearLeft, true);
		//driveBase.setInvertedMotor(MotorType.kRearRight, true);
		// might need to invert some motors
	}

	@Override
	public void update() {
		switch(driveState){
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

	public void setManualDrive(double moveValue, double turnValue) {
		if(driveState != DriveState.MANUAL){
			driveState = DriveState.MANUAL;
			configureTalons(TalonControlMode.PercentVbus);
		}
		// low2 + (value - low1) * (high2 - low2) / (high1 - low1)
		if(Math.abs(moveValue) >= MINIMUM_INPUT){
			moveValue = (moveValue * (Math.abs(moveValue) - MINIMUM_INPUT)) / ((MAXIMUM_INPUT - MINIMUM_INPUT) * Math.abs(moveValue));
			// setWheelVelocity(new DriveVelocity(60, 0));
			// moveValue * (MINIMUM_OUTPUT + (Math.abs(moveValue) - MINIMUM_INPUT) * (MAXIMUM_OUTPUT - MINIMUM_OUTPUT)) / (MAXIMUM_INPUT - MINIMUM_INPUT) * Math.abs(moveValue);
			// Correct way but we can take out MINIMUM_OUTPUT in the front because it will be 0 and also the (MAXIMUM_OUTPUT - MINIMUM_OUTPUT) because that will amount to 1
			
		}
		
		if(Math.abs(turnValue) >= MINIMUM_INPUT){
			turnValue = turnValue * (Math.abs(turnValue) - MINIMUM_INPUT) / (MAXIMUM_INPUT - MINIMUM_INPUT) * Math.abs(turnValue);
		}		
		System.out.println("left " + leftWheel.getSpeed() + "right " + rightWheel.getSpeed());
		driveBase.arcadeDrive(moveValue, turnValue);
	}	

	public void setAutoPath(Path autoPath){
		if(driveState != DriveState.AUTO){
			driveState = DriveState.AUTO;
			configureTalons(TalonControlMode.Speed);
		}
		// PurePursuitController(double lookAheadDistance, double robotSpeed, double robotDiameter, Path robotPath)
		autonomousDriver = new PurePursuitController(10, 10, 10, autoPath);		
		updateAutoPath();
	}
	
	public void setGearPath() {
		if(driveState != DriveState.GEAR){
			driveState = DriveState.GEAR;
			configureTalons(TalonControlMode.Speed);
		}
		
		isDone = false;
		desiredAngle = testGyro.getAngle() + Dashcomm.get("angle", 0);
	}
	
	private void setWheelVelocity(DriveVelocity setVelocity){
		leftWheel.setSetpoint(setVelocity.wheelSpeed + setVelocity.deltaSpeed);
		rightWheel.setSetpoint(setVelocity.wheelSpeed - setVelocity.deltaSpeed);
		//System.out.println("left " + leftWheel.getSpeed() + "right " + rightWheel.getSpeed());
	}
	
	public boolean isDone(){
		switch(driveState){
		case AUTO:
			// check if path is completed
			return autonomousDriver.isDone(robotState.getCurrentPosition());
		case GEAR:
			return isDone;
		}
		return true;
	}
	
	private void updateAutoPath(){		
		autoDriveVelocity = autonomousDriver.calculate(robotState.getCurrentPosition());
		setWheelVelocity(autoDriveVelocity);
		
	}
	
	public void updateGearPath(){
		if(desiredAngle - testGyro.getAngle() > 2 ){
			// TODO: Angle per sec to inch per sec to rotations per sec
			// These are arbitrary values
			// Check if it's done
			DriveVelocity turningSpeed = new DriveVelocity(0, 2);
			setWheelVelocity(turningSpeed);
		} else if (desiredAngle - testGyro.getAngle() < -2) {
			DriveVelocity turningSpeed = new DriveVelocity(0, -2);
			setWheelVelocity(turningSpeed);
		} else {
			DriveVelocity drivingSpeed = new DriveVelocity(2, 0);
			setWheelVelocity(drivingSpeed);
		}
	}
	
	public void configureTalons(TalonControlMode mode){
		leftWheel.changeControlMode(mode);
		rightWheel.changeControlMode(mode);		
	}
	
	/*
	private static double angleToInchesPerSecond(){
		// diameter * pi
		// times angle per sec
		// divided by 360
	}
	*/
	
	private static double  inchesPerSecondToRpm(double inchesPerSec){
		return inchesPerSec / (4 * Math.PI) * 60;
		// 5 should be the wheel diameter
	}
	
	// TODO: Return wheel in inches or something
	public double getLeftDistance(){
		return leftWheel.getPosition();
	}
	
	public double getRightDistance(){
		return rightWheel.getPosition();
	}
	
	public static class DriveVelocity {
		
		public double wheelSpeed;
		public double deltaSpeed;
		
		public DriveVelocity(double wheelSpeed, double deltaSpeed){
			this.wheelSpeed = wheelSpeed;
			this.deltaSpeed = deltaSpeed;
		}
		
	}

}
