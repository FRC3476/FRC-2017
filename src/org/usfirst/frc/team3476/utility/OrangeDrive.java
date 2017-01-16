package org.usfirst.frc.team3476.utility;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SpeedController;

public class OrangeDrive extends RobotDrive
{
	private double move, turn;
	private double TURN_DEAD = .33;
	private double MOVE_DEAD = 0;

	public OrangeDrive(int frontLeftMotor, int rearLeftMotor, int frontRightMotor, int rearRightMotor)
	{
		super(frontLeftMotor, rearLeftMotor, frontRightMotor, rearRightMotor);
	}

	public OrangeDrive(int leftMotorChannel, int rightMotorChannel)
	{
		super(leftMotorChannel, rightMotorChannel);
	}

	public OrangeDrive(SpeedController frontLeftMotor, SpeedController rearLeftMotor, SpeedController frontRightMotor,
			SpeedController rearRightMotor)
	{
		super(frontLeftMotor, rearLeftMotor, frontRightMotor, rearRightMotor);
	}

	public OrangeDrive(SpeedController leftMotor, SpeedController rightMotor)
	{
		super(leftMotor, rightMotor);
	}
	
	public void setMove(double move)
	{
		this.move = move;
		updateDrive();
	}
	public void setTurn(double turn)
	{
		this.turn = turn;
		updateDrive();
	}
	
	public synchronized void updateDrive()
	{
		OrangeUtility.scalingDonut(turn, TURN_DEAD, 1, 1);
		OrangeUtility.scalingDonut(move, MOVE_DEAD, 1, 1);
		super.arcadeDrive(move, turn);
	}
}

