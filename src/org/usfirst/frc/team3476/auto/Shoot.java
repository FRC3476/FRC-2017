package org.usfirst.frc.team3476.auto;

import org.usfirst.frc.team3476.subsystem.Flywheel;

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
		
	}

	@Override
	public boolean isDone() {
		
	}

}
