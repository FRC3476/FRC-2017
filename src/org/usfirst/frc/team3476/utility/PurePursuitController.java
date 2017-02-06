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
		double radius = robotPath.getRadius(robotState, lookAheadDistance);
		double deltaSpeed = robotDiameter * (robotSpeed / radius) / 2;
		
		return new OrangeDrive.DriveVelocity(robotSpeed, deltaSpeed);
		// why does this work?????
		// robotdiameter * (speed / radius) / 2
		// ^ wheel speed delta
		
		// TODO: 
		// scale to max speed
	}
	
	public boolean isDone(RigidTransform robotState){
		// 5 is min off in inches
		if(robotPath.getDistanceTo(robotState.translationMat, robotPath.endPoint()) < 5){
			return true;
		} else {
			return false;
		}
	}
}
