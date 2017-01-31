package org.usfirst.frc.team3476.subsystem;

public class RobotTracker {
	
	private RobotTracker trackingInstance = new RobotTracker();
	
	public RobotTracker getInstance(){
		return trackingInstance;
	}
	
	private RobotTracker (){
		
	}
}
