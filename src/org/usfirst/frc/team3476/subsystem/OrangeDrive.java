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
	public enum DriveState{MANUAL, AUTO}
	private DriveState currentState;
	private static int DRIVERUNNINGSPEED = 50;
	private double oldTime;
	
	
	public OrangeDrive(int frontLeftMotor, int rearLeftMotor, int frontRightMotor, int rearRightMotor)
	{
		RUNNINGSPEED = DRIVERUNNINGSPEED;
		driveBase = new RobotDrive(frontLeftMotor, rearLeftMotor, frontRightMotor, rearRightMotor);
	}

	public OrangeDrive(int leftMotorChannel, int rightMotorChannel)
	{
		RUNNINGSPEED = DRIVERUNNINGSPEED;
		driveBase = new RobotDrive(leftMotorChannel, rightMotorChannel);
	}

	public OrangeDrive(SpeedController frontLeftMotor, SpeedController rearLeftMotor, SpeedController frontRightMotor,
			SpeedController rearRightMotor)
	{
		RUNNINGSPEED = DRIVERUNNINGSPEED;
		driveBase = new RobotDrive(frontLeftMotor, rearLeftMotor, frontRightMotor, rearRightMotor);
	}

	public OrangeDrive(SpeedController leftMotor, SpeedController rightMotor)
	{
		RUNNINGSPEED = DRIVERUNNINGSPEED;
		driveBase = new RobotDrive(leftMotor, rightMotor);
	}
	
	public synchronized void setMove(double move)
	{
		this.move = move;
		updateDrive();
	}
	public synchronized void setTurn(double turn)
	{
		this.turn = turn;
		updateDrive();
	}
	
	
	public void manualDrive(double move, double turn){
		this.move = move;
		this.turn = turn;
	}
	
	public void setState(DriveState driveState){
		this.currentState = driveState;
	}
	
	public synchronized void run(){
		if (System.currentTimeMillis() - oldTime > 50)
			updateDrive();
		
	}
	
	private void updateDrive(){		
		oldTime = System.currentTimeMillis();
		driveBase.arcadeDrive(OrangeUtility.scalingDonut(move, MOVE_DEAD, 1, 1), OrangeUtility.scalingDonut(turn, TURN_DEAD, 1, 1));
	}
	
	public void centerOnGear(){
		
	}
	
	
}

