package org.usfirst.frc.team3476.auto;

import org.usfirst.frc.team3476.subsystem.OrangeDrive;
import org.usfirst.frc.team3476.subsystem.RobotTracker;
import org.usfirst.frc.team3476.utility.Rotation;

public class SetGyroOffset implements Action{
	
	private Rotation offset;
	
	public SetGyroOffset(double wantedOffset){
		offset = Rotation.fromDegrees(wantedOffset);
	}

	@Override
	public void start() {
		OrangeDrive.getInstance().setOffset(offset);
		RobotTracker.getInstance().resetPose();
	}

	@Override
	public boolean isDone() {
		return true;
	}
	
	
}
