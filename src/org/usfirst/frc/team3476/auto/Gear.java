package org.usfirst.frc.team3476.auto;

import org.usfirst.frc.team3476.subsystem.OrangeDrive;

public class Gear implements Action{
	
	public Gear() {
	}
	
	@Override
	public void start() {
		OrangeDrive.getInstance().setGearPath();
		
	}

	@Override
	public boolean isDone() {
		return OrangeDrive.getInstance().isDone();
	}

}
