package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.utility.OrangeDrive;
import org.usfirst.frc.team3476.utility.OrangeDrivePIDWrapper;
import org.usfirst.frc.team3476.utility.OrangeUtility;
import org.usfirst.frc.team3476.utility.PIDDashdataWrapper;
import org.usfirst.frc.team3476.utility.Threaded;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SpeedController;

public class Drive extends Threaded
{	
	private CANTalon m_topLeftMotor;
	private CANTalon m_topRightMotor;
	private CANTalon m_bottomLeftMotor;
	private CANTalon m_bottomRightMotor;
	private OrangeDrive driveBase;
	private PIDController pidTurn;
	private double moveVal = 0, turnVal = 0;
	public enum DriveState {MANUAL,	PEG}
	private DriveState currentState = DriveState.MANUAL;
	
	
	public Drive(CANTalon topLeftMotor, CANTalon bottomLeftMotor, CANTalon topRightMotor, CANTalon bottomRightMotor)
	{
		RUNNINGSPEED = 50;
		m_topLeftMotor = topLeftMotor;
		m_topRightMotor = topRightMotor;
		m_bottomLeftMotor = bottomLeftMotor;
		m_bottomRightMotor = bottomRightMotor;
		driveBase =  new OrangeDrive(m_topLeftMotor, m_topRightMotor, m_bottomLeftMotor, m_bottomRightMotor);
		System.out.println("orangeDrive created");
	}

	@Override
	public void run()
	{
		System.out.println("0");
		switch (currentState)
		{
			case MANUAL:
				//Manual driving
				System.out.println("1");
				pidTurn.disable();
	    		driveBase.arcadeDrive(moveVal, turnVal);
				break;
			case PEG:
				//Peg tracking shit goes here
				//pidTurn.enable();
	    		//pidTurn.setSetpoint(0);
				System.out.println("Peg");
				break;
		}
	}
	
	public void createPIDController(double p, double i, double d, String sourceKeypath, double seconds)
	{
	    OrangeDrivePIDWrapper turn = new OrangeDrivePIDWrapper(driveBase, OrangeDrivePIDWrapper.Axis.TURN);
	    OrangeDrivePIDWrapper move = new OrangeDrivePIDWrapper(driveBase, OrangeDrivePIDWrapper.Axis.MOVE);
	    PIDDashdataWrapper source = new PIDDashdataWrapper(sourceKeypath);
	    pidTurn = new PIDController(p, i, d, source, turn, seconds);
	}
	
	public void setState(DriveState state)
	{
		currentState = state;
	}
	
	public void setPID(double p, double i, double d)
	{
		pidTurn.setPID(p, i, d);
	}
	
	public void move(double move)
	{
		driveBase.setMove(move);
	}
	
	public void turn(double turn)
	{
		driveBase.setTurn(turn);
	}
	
	public void updateDriveValues(double move, double turn)
	{
		this.moveVal = move;
		this.turnVal = turn;
	}
	
}
