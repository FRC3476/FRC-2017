package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.utility.Constants;
import org.usfirst.frc.team3476.utility.Threaded;

public class VisionTracking extends Threaded {

	private Turret rightTurret;
	private Turret leftTurret;
	private Flywheel leftFlywheel;
	private Flywheel rightFlywheel;

	//fix to use singleton pattern
	private VisionTracking() {
		rightTurret = new Turret(Constants.RightTurretId);
		leftTurret = new Turret(Constants.LeftTurretId);
		
		leftFlywheel = new Flywheel(Constants.LeftMasterFlywheelId, Constants.LeftSlaveFlywheelId, Constants.LeftBallSensorId);
		rightFlywheel = new Flywheel(Constants.RightMasterFlywheelId, Constants.RightSlaveFlywheelId, Constants.RightBallSensorId);
	}

	@Override
	public void update() {

	}

	public boolean isDone() {
		return false;
	}
}
