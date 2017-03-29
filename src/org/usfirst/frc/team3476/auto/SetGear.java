package org.usfirst.frc.team3476.auto;

import org.usfirst.frc.team3476.subsystem.Gear;
import org.usfirst.frc.team3476.subsystem.Gear.GearState;

public class SetGear implements Action{
	
	GearState wantedState;
	
	public SetGear(GearState state){
		wantedState = state;
	}
	
	@Override
	public void start() {
		Gear.getInstance().setState(wantedState);
	}

	@Override
	public boolean isDone() {
		return Gear.getInstance().isDone();
	}

}
