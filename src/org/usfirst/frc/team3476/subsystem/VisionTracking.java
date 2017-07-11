package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.utility.Constants;
import org.usfirst.frc.team3476.utility.Dashcomm;
import org.usfirst.frc.team3476.utility.Interpolable;
import org.usfirst.frc.team3476.utility.Rotation;
import org.usfirst.frc.team3476.utility.Threaded;
import org.usfirst.frc.team3476.utility.Translation;

public class VisionTracking extends Threaded {

	private static final VisionTracking trackingInstance = new VisionTracking();
	
	private Rotation gearAngle;
	private Rotation turretAngle;
	private Interpolable lookupTable;
	private double desiredFlywheelSpeed;
	
	
	public static VisionTracking getInstance(){
		return trackingInstance;
	}
	
	private VisionTracking() {
		RUNNINGSPEED = 10;
		lookupTable = new Interpolable();
		lookupTable.addNumber(10.0, 10.0);
		
	}

	@Override
	public synchronized void update() {
		/*
		 * Plan is to only collect values and have a function to interpolate in real time
		 
		if(Dashcomm.get("isGearVisible", 0) != 0){
			double cameraAngle = Dashcomm.get("angle", 0);
			double distance = Dashcomm.get("distance", 0);
			Translation targetPosition = Translation.fromAngleDistance(distance, Rotation.fromDegrees(cameraAngle)).rotateBy(Rotation.fromDegrees(Constants.CameraAngleOffset));
			Translation offset = new Translation(5.5, -10);
			gearAngle = OrangeDrive.getInstance().getGyroAngle().rotateBy(offset.getAngleTo(targetPosition).inverse());
		} else {
			gearAngle = OrangeDrive.getInstance().getGyroAngle();
		}
		
		*/
		
		if(Dashcomm.get("isBoilerVisible", 0) != 0){
			//Dashcomm.get("boilerXAngle", 0);
			desiredFlywheelSpeed = lookupTable.interpolate(Dashcomm.get("boilerYAngle", 0));			
		}	
	}
	
	public double getBoilerDistance(){
		double x = 1;
		double y = Dashcomm.get("boilerX", 0);
		double z = Dashcomm.get("boilerY", 0);		
		double distance = Constants.BoilerHeight / Math.tan(Math.toRadians(z / 1280 * Constants.yCameraFOV + "camera angle upwards")) //to radians first
		return distance;		
		/*
		x is forwards from camera
		y is to the left from camera
		z is up from the camera
		x = x * yawOffset.cos() - x * yawOffset.sin();
		y = y * yawOffset.cos() + y * yawOffset.sin();
		
		x = x * pitchOffset.cos() - x * pitchOffset.sin();
		z = z * pitchOffset.cos() + z * pitchOffset.sin();
		
		distance = Constants.BoilerHeight / z * Math.hypot(x, y);			
		*/
	}	
	
	public synchronized double getFlywheelSpeed(){
		return desiredFlywheelSpeed;
	}
}
