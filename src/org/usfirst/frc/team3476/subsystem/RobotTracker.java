package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.utility.RigidTransform;
import org.usfirst.frc.team3476.utility.Rotation;
import org.usfirst.frc.team3476.utility.Threaded;
import org.usfirst.frc.team3476.utility.Translation;

public class RobotTracker extends Threaded {
	
	private RobotTracker trackingInstance = new RobotTracker();
	private RigidTransform latestPosition;
	
	public RobotTracker getInstance(){
		return trackingInstance;
	}
	
	private RobotTracker (){
		latestPosition = new RigidTransform(new Rotation(), new Translation());		
	}

	@Override
	public void update() {
		
	}
	
	public RigidTransform getCurrentPosition(){
		return latestPosition;
	}
	
	
}
