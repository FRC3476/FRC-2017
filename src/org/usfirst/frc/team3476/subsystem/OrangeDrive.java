package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.utility.OrangeDrivePIDWrapper;
import org.usfirst.frc.team3476.utility.OrangeDrivePIDWrapper.Axis;
import org.usfirst.frc.team3476.utility.OrangeUtility;
import org.usfirst.frc.team3476.utility.PIDDashdataWrapper;
import org.usfirst.frc.team3476.utility.Threaded;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.RobotDrive;

public class OrangeDrive extends Threaded {
	public enum DriveState {
		MANUAL, AUTO
	}
	private DriveState currentState = DriveState.MANUAL;
	
	private double moveValue, turnValue;
	private static final double TURN_DEAD = .33;
	private static final double MOVE_DEAD = 0;

	private RobotDrive driveBase;
	private AnalogGyro testGyro = new AnalogGyro(0);
	private OrangeDrivePIDWrapper turnOutput = new OrangeDrivePIDWrapper(this, Axis.TURN);
	private PIDController turnPid = new PIDController(0.05, 0, 0, testGyro, turnOutput);	
	private PIDDashdataWrapper angleOffset = new PIDDashdataWrapper("angle");
	private CANTalon leftWheel, rightWheel;
	
	// TODO: Make a centralize place to set and get constants
	private static OrangeDrive driveInstance = new OrangeDrive(7, 8, 4, 5);

	public static OrangeDrive getInstance() {
		return driveInstance;
	}

	private OrangeDrive(int frontLeftMotor, int rearLeftMotor, int frontRightMotor, int rearRightMotor) {
		RUNNINGSPEED = 50;
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

	public void setManualDrive(double moveValue, double turnValue) {
		if(currentState != DriveState.MANUAL){
			currentState = DriveState.MANUAL;
			configureTalons(TalonControlMode.PercentVbus);
		}
		this.moveValue = moveValue;
		this.turnValue = turnValue;
		setArcadeDrive();
	}

	@Override
	public void update() {
		switch(currentState){
		case MANUAL:
			if(turnPid.isEnabled()){
				turnPid.disable();
			}
			break;
		case AUTO:
			updateAutoDrive();
			break;		
		}
	}

	private void setArcadeDrive() {
		driveBase.arcadeDrive(OrangeUtility.scalingDonut(moveValue, MOVE_DEAD, 1, 1), OrangeUtility.scalingDonut(turnValue, TURN_DEAD, 1, 1));
	}

	// TODO: 2D Coordinates
	public void setWaypoint(double angle, double distance){
		if(currentState != DriveState.AUTO){
			currentState = DriveState.AUTO;
		//	configureTalons(TalonControlMode.Speed);
		}	
		// TODO: Code for tracking distance traveled
		turnPid.setSetpoint(angle);	
		//updateAutoDrive();
	}
	
	// TODO: Wheel Velocity should be one object sent
	private void setWheelVelocity(double leftWheelVelocity, double rightWheelVelocity){
		leftWheel.set(leftWheelVelocity);
		rightWheel.set(rightWheelVelocity);
	}
	
	private void updateAutoDrive(){
		turnPid.enable();
		
		// TODO: Make some shit to calculate velocity for wheels ie. PID Controller
		//setWheelVelocity(leftWheelVelocity, rightWheelVelocity);
	}
	
	public void configureTalons(TalonControlMode mode){
		leftWheel.changeControlMode(mode);
		rightWheel.changeControlMode(mode);		
	}
	
	public void centerOnGear() {
		setWaypoint(testGyro.getAngle() + angleOffset.pidGet(), 0);
	}

}
