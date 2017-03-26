package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.utility.Constants;
import org.usfirst.frc.team3476.utility.Dashcomm;
import org.usfirst.frc.team3476.utility.Rotation;
import org.usfirst.frc.team3476.utility.Threaded;
import org.usfirst.frc.team3476.utility.Translation;

public class VisionTracking extends Threaded {

	private static final VisionTracking trackingInstance = new VisionTracking();
	
	private Rotation gearAngle;
	private Rotation turretAngle;
	
	public static VisionTracking getInstance(){
		return trackingInstance;
	}
	
	private VisionTracking() {
		RUNNINGSPEED = 10;
		update();
	}

	@Override
	public void update() {
		/*
		 * Plan is to only collect values and have a function to interpolate in real time
		 */
		if(Dashcomm.get("isVisible", 0) != 0){
			double cameraAngle = Dashcomm.get("angle", 0);
			double distance = Dashcomm.get("distance", 0);
			Translation targetPosition = Translation.fromAngleDistance(distance, Rotation.fromDegrees(cameraAngle)).rotateBy(Rotation.fromDegrees(Constants.CameraAngleOffset));
			Translation offset = new Translation(5.5, -10);
			gearAngle = OrangeDrive.getInstance().getGyroAngle().rotateBy(offset.getAngleTo(targetPosition).inverse());
		} else {
			gearAngle = OrangeDrive.getInstance().getGyroAngle();
		}
		
		
	}
	
	// function will later interpolate based on current position
	public Rotation getGearAngle(){
		return gearAngle;
	}
	

	public Rotation getFiringSolution(){
		// change to return distance + angle
		return turretAngle;
	}
	
	

}
