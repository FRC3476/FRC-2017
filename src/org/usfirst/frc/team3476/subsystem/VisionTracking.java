package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.utility.Constants;
import org.usfirst.frc.team3476.utility.Threaded;

public class VisionTracking extends Threaded implements Action{

	private Turret leftTurret;
	private Turret rightTurret;
	
	private Flywheel leftFlywheel = new Flywheel(Constants.LeftMasterFlywheelId, Constants.LeftSlaveFlywheelId);
	
	@Override
	public void update() {
		
	}

	public boolean isDone()
	{
		return false;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	
}
