package org.usfirst.frc.team3476.auto;

import org.usfirst.frc.team3476.subsystem.OrangeDrive;

public class PutGearOn implements Action {

	OrangeDrive drive = OrangeDrive.getInstance();

	public PutGearOn() {
	}

	@Override
	public void start() {
		drive.setGearPath();

	}

	@Override
	public boolean isDone() {
		return drive.isDone();
	}

}
