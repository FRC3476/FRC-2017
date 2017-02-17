package org.usfirst.frc.team3476.auto;

import org.usfirst.frc.team3476.subsystem.OrangeDrive;

public class Gear implements Action {

	OrangeDrive drive = OrangeDrive.getInstance();

	public Gear() {
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
