package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.utility.OrangeDrivePIDWrapper;
import org.usfirst.frc.team3476.utility.OrangeDrivePIDWrapper.Axis;
import org.usfirst.frc.team3476.utility.OrangeUtility;
import org.usfirst.frc.team3476.utility.PIDDashdataWrapper;
import org.usfirst.frc.team3476.utility.Threaded;

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
	// TODO: Add a movePid and replace both with a PIDController that doesn't run on it's own thread
	// TODO: Make a centralize place to set and get constants
	private static OrangeDrive driveInstance = new OrangeDrive(7, 8, 4, 5);

	public static OrangeDrive getInstance() {
		return driveInstance;
	}

	private OrangeDrive(int frontLeftMotor, int rearLeftMotor, int frontRightMotor, int rearRightMotor) {
		RUNNINGSPEED = 50;
		driveBase = new RobotDrive(frontLeftMotor, rearLeftMotor, frontRightMotor, rearRightMotor);
	}

	public void setManualDrive(double moveValue, double turnValue) {
		if(currentState != DriveState.MANUAL){
			currentState = DriveState.MANUAL;
		}
		this.moveValue = moveValue;
		this.turnValue = turnValue;
		setArcadeDrive();
	}

	@Override
	public void update() {
		switch(currentState){
		case MANUAL:
			break;
		case AUTO:
			updateAutoDrive();
			break;		
		}
	}

	private void setArcadeDrive() {
		driveBase.arcadeDrive(OrangeUtility.scalingDonut(moveValue, MOVE_DEAD, 1, 1), OrangeUtility.scalingDonut(turnValue, TURN_DEAD, 1, 1));
	}

	public void setWaypoint(double angle, double distance){
		if(currentState != DriveState.AUTO){
			currentState = DriveState.AUTO;
		}	
		// TODO: Set angle/distance in PIDController here
	}
	
	private void updateAutoDrive(){
		// TODO: Add PIDController that doesn't run on it's own thread so we can call calculate();
	}
	public void centerOnGear() {
		setWaypoint(testGyro.getAngle() + angleOffset.pidGet(), 0);
	}

}
