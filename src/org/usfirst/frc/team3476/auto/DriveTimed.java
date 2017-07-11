package org.usfirst.frc.team3476.auto;

import org.usfirst.frc.team3476.subsystem.OrangeDrive;

public class DriveTimed implements Action {
	double speed;
	double time;
	
	public DriveTimed(double drivingSpeed, double timeTilStop){
		speed = drivingSpeed;
		time = timeTilStop;
	}
	
	@Override
	public void start() {
		OrangeDrive.getInstance().setAutoTime(speed, time);
	}

	@Override
	public boolean isDone() {
		return OrangeDrive.getInstance().isDone();
	}	
}
