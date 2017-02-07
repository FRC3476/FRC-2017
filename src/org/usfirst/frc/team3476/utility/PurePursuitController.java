package org.usfirst.frc.team3476.utility;

import org.usfirst.frc.team3476.subsystem.OrangeDrive;

public class PurePursuitController {
	/*
	1. Translation delta compared to robot
	2. Check x offset * angle to see if past tolerance
	3. Create circle
	4. Follow circle path
	*/
	
	private double lookAheadDistance;
	private double robotSpeed;
	private double robotDiameter;
	private Path robotPath;
	
	
	public PurePursuitController(double lookAheadDistance, double robotSpeed, double robotDiameter, Path robotPath){
		this.lookAheadDistance = lookAheadDistance;
		this.robotSpeed = robotSpeed;
		this.robotDiameter = robotDiameter;
		this.robotPath = robotPath;
		
	}
	
	public OrangeDrive.DriveVelocity calculate(RigidTransform robotState){
		double radius = getRadius(robotState, lookAheadDistance);
		double deltaSpeed = robotDiameter * (robotSpeed / radius) / 2;
		
		return new OrangeDrive.DriveVelocity(robotSpeed, deltaSpeed);
		// why does this work?????
		// robotdiameter * (speed / radius) / 2
		// ^ wheel speed delta
		
		// TODO: 
		// scale to max speed
	}
	
	public double getRadius(RigidTransform robotPosition, double lookAheadDistance){
		// Get point if robot was centered on 0 degrees
		Translation lookAheadPoint = robotPath.getLookAheadPoint(robotPosition.translationMat, lookAheadDistance).rotateBy(robotPosition.rotationMat.inverse());		
		
		// check if it is straight ahead or not
		if(Math.abs(lookAheadPoint.getX() - robotPosition.translationMat.getX()) < 1){
			return 0;
		}
		double radius = Math.pow(lookAheadPoint.getDistanceTo(robotPosition.translationMat), 2) / (2 * lookAheadPoint.getX());
		if(lookAheadPoint.getX() > 0){
			return radius;
		} else {
			return -radius;
		}
	}
	
	public boolean isDone(RigidTransform robotState){
		// 5 is min off in inches
		if(robotState.translationMat.getDistanceTo(robotPath.endPoint()) < 5){
			return true;
		} else {
			return false;
		}
	}
}
