package org.usfirst.frc.team3476.auto;

import org.usfirst.frc.team3476.subsystem.OrangeDrive;

public class PutGearOn implements Action {

	public PutGearOn() {
	}

	@Override
	public boolean isDone() {
		return OrangeDrive.getInstance().isDone();
	}

	@Override
	public void start() {
		OrangeDrive.getInstance().setGearPath();

	}

}
