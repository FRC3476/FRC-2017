package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.utility.Constants;
import org.usfirst.frc.team3476.utility.Threaded;

public class VisionTracking extends Threaded {

	private Turret rightTurret;	
	private Flywheel leftFlywheel;
	
	private VisionTracking(){
		leftFlywheel = new Flywheel(Constants.LeftMasterFlywheelId, Constants.LeftSlaveFlywheelId, Constants.LeftBallSensorId);
	}
	
	@Override
	public void update() {
		
	}

	public boolean isDone()
	{
		return false;
	}
}
