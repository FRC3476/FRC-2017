package org.usfirst.frc.team3476.utility;

import org.usfirst.frc.team3476.subsystem.OrangeDrive;
import org.usfirst.frc.team3476.subsystem.OrangeDrive.DriveVelocity;

public class PurePursuitController {
	/*
	1. Translation delta compared to robot
	2. Check x offset * angle to see if past tolerance
	3. Create circle
	4. Follow circle path
	*/

	private double circleRadius;
	
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
		// calculate difference from path
		// find angle of path away from zero (asin(distance to next waypoint / nextwaypoint y))
		// rotate robotPosition by angle of path away
		// y should be distance away
		// calculate angle from path using height
		// find distance in path
		// add lookahead distance ez
		double radius = robotPath.getRadius(robotState.translationMat, lookAheadDistance);
		double deltaSpeed = robotDiameter * (robotSpeed / radius) / 2;
		
		return new OrangeDrive.DriveVelocity(robotSpeed, deltaSpeed);
		// why does this work?????
		// robotdiameter * (speed / radius) / 2
		// ^ wheel speed delta
		
		// TODO: 
		// scale to max speed
	}
}
