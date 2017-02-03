package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.utility.Dashcomm;
import org.usfirst.frc.team3476.utility.OrangeUtility;
import org.usfirst.frc.team3476.utility.Threaded;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.RobotDrive;

	/* Much inspiration from Team 254 */

public class OrangeDrive extends Threaded {
	public enum DriveState {
		MANUAL, AUTO, GEAR
	}
	private DriveState driveState = DriveState.MANUAL;
	
	private double moveValue, turnValue;
	private double desiredAngle;
	private final double TURN_DEAD = .33;
	private final double MOVE_DEAD = 0;

	private RobotDrive driveBase;
	private AnalogGyro testGyro = new AnalogGyro(0);
	private CANTalon leftWheel, rightWheel;
	private RobotTracker robotState = RobotTracker.getInstance();
	
	// TODO: Make a centralize place to set and get constants
	private static OrangeDrive driveInstance = new OrangeDrive(7, 8, 4, 5);
	

	public static OrangeDrive getInstance() {
		return driveInstance;
	}

	private OrangeDrive(int frontLeftMotor, int rearLeftMotor, int frontRightMotor, int rearRightMotor) {
		RUNNINGSPEED = 10;
		leftWheel = new CANTalon(frontLeftMotor);
		rightWheel = new CANTalon(frontRightMotor);
		
		CANTalon leftSlaveWheel = new CANTalon(rearLeftMotor);
		CANTalon rightSlaveWheel = new CANTalon(rearRightMotor);
		
		leftSlaveWheel.changeControlMode(TalonControlMode.Follower);
		leftSlaveWheel.set(frontLeftMotor);
		rightSlaveWheel.changeControlMode(TalonControlMode.Follower);
		rightSlaveWheel.set(frontRightMotor);
		
		driveBase = new RobotDrive(frontLeftMotor, rearLeftMotor, frontRightMotor, rearRightMotor);
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
		this.moveValue = moveValue;
		this.turnValue = turnValue;
		updateArcadeDrive();
	}	

	public void setAutoPath(double angle, double distance){
		if(driveState != DriveState.AUTO){
			driveState = DriveState.AUTO;
			configureTalons(TalonControlMode.Speed);
		}
		robotState.getCurrentPosition();
		// TODO: Setup Pure Pursuit Controller		
		updateAutoPath();
	}
	
	public void setGearPath() {
		if(driveState != DriveState.GEAR){
			driveState = DriveState.GEAR;
			configureTalons(TalonControlMode.Speed);
		}
		desiredAngle = testGyro.getAngle() + Dashcomm.get("angle", 0);
	}
	
	private void setWheelVelocity(DriveVelocity setVelocity){
		leftWheel.set(setVelocity.wheelSpeed + setVelocity.deltaSpeed);
		rightWheel.set(setVelocity.wheelSpeed - setVelocity.deltaSpeed);
	}
	
	private void updateArcadeDrive() {
		driveBase.arcadeDrive(OrangeUtility.scalingDonut(moveValue, MOVE_DEAD, 1, 1), OrangeUtility.scalingDonut(turnValue, TURN_DEAD, 1, 1));
	}
	
	private void updateAutoPath(){		
		// TODO: Pure Pursuit Controller
		
		//setWheelVelocity();
	}
	
	public void updateGearPath(){
		if(desiredAngle - testGyro.getAngle() > 2 ){
			// TODO: Angle per sec to inch per sec to rotations per sec
			// These are arbitrary values
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
		return inchesPerSec / (5 * Math.PI) * 60;
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
