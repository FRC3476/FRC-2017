package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.utility.OrangeUtility;
import org.usfirst.frc.team3476.utility.Threaded;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SpeedController;

public class OrangeDrive extends Threaded
{
	private double move, turn, pidMove, pidTurn;
	private double TURN_DEAD = .33;
	private double MOVE_DEAD = 0;
	private RobotDrive driveBase;
	public enum DriveState{MANUAL, AUTO};
	private DriveState currentState;
	
	public OrangeDrive(int frontLeftMotor, int rearLeftMotor, int frontRightMotor, int rearRightMotor)
	{
		RUNNINGSPEED = 50;
		driveBase = new RobotDrive(frontLeftMotor, rearLeftMotor, frontRightMotor, rearRightMotor);
	}

	public OrangeDrive(int leftMotorChannel, int rightMotorChannel)
	{
		RUNNINGSPEED = 50;
		driveBase = new RobotDrive(leftMotorChannel, rightMotorChannel);
	}

	public OrangeDrive(SpeedController frontLeftMotor, SpeedController rearLeftMotor, SpeedController frontRightMotor,
			SpeedController rearRightMotor)
	{
		RUNNINGSPEED = 50;
		driveBase = new RobotDrive(frontLeftMotor, rearLeftMotor, frontRightMotor, rearRightMotor);
	}

	public OrangeDrive(SpeedController leftMotor, SpeedController rightMotor)
	{
		RUNNINGSPEED = 50;
		driveBase = new RobotDrive(leftMotor, rightMotor);
	}
	
	public void setMove(double move)
	{
		this.move = move;
	}
	public void setTurn(double turn)
	{
		this.turn = turn;
	}
	
	public void pidSetMove(double move){
		this.pidMove = move;
	}
	
	public void pidSetTurn(double turn){
		this.pidTurn = turn;
	}
	
	public void updateDriveValues(double move, double turn){
		this.move = move;
		this.turn = turn;
	}
	
	public void setState(DriveState driveState){
		this.currentState = driveState;
	}
	
	public synchronized void run()
	{
		switch(currentState){
		case MANUAL:
			driveBase.arcadeDrive(move, turn);
		case AUTO:
			OrangeUtility.scalingDonut(pidTurn, TURN_DEAD, 1, 1);
			OrangeUtility.scalingDonut(pidMove, MOVE_DEAD, 1, 1);
			driveBase.arcadeDrive(pidMove, pidTurn);
		}
	}
}

