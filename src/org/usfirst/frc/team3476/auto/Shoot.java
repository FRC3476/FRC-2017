package org.usfirst.frc.team3476.auto;

import org.usfirst.frc.team3476.subsystem.Flywheels;

public class Shoot implements Action{
	int speedleft;
	int speedright;
	public Shoot(int leftspeed, int rightspeed) {
		speedleft = leftspeed;
		speedright = rightspeed;
	}
	
	public Shoot(int speed) {
		speedleft = speedright = speed;
	}
	
	@Override
	public void start() {
		Flywheels.getInstance().setLeftSetpoint(speedleft);
		Flywheels.getInstance().setRightSetpoint(speedright);
	}

	@Override
	public boolean isDone() {
		return Flywheels.getInstance().isDone();
	}

}
