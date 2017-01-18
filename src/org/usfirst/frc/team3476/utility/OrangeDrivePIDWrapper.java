package org.usfirst.frc.team3476.utility;

import org.usfirst.frc.team3476.subsystem.OrangeDrive;

import edu.wpi.first.wpilibj.PIDOutput;

public class OrangeDrivePIDWrapper implements PIDOutput
{

	public enum Axis{MOVE, TURN};
	private OrangeDrive drive;
	private Axis axis;
	
	@Override
	public void pidWrite(double output)
	{
		switch(axis)
		{
			case MOVE:
				drive.pidSetMove(output);
				break;
				
			case TURN:
				drive.pidSetTurn(output);
				break;
		}
	}
	
	public OrangeDrivePIDWrapper(OrangeDrive driveBase, Axis movementAxis)
	{
		this.drive = driveBase;
		this.axis = movementAxis;
	}
}
