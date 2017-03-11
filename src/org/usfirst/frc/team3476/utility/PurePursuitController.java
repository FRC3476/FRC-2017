package org.usfirst.frc.team3476.utility;

import org.usfirst.frc.team3476.subsystem.OrangeDrive;

public class PurePursuitController {
	/*
	 * 1. Translation delta compared to robot
	 * 2. Check x offset * angle to see if past tolerance
	 * 3. Create circle
	 * 4. Follow circle path
	 * 5. Actual path looks like a spline
	 */
	
	private double lookAheadDistance;
	private double robotSpeed;
	private double robotDiameter;
	private Path robotPath;
	private boolean isReversed;

	public PurePursuitController(double lookAheadDistance, double robotSpeed, double robotDiameter, Path robotPath, boolean isReversed) {
		this.lookAheadDistance = lookAheadDistance;
		this.robotSpeed = robotSpeed;
		this.robotDiameter = robotDiameter;
		this.robotPath = robotPath;
		this.isReversed = isReversed;

	}

	public OrangeDrive.DriveVelocity calculate(RigidTransform robotPose) {
		
		if(isReversed){
			robotPose = new RigidTransform(robotPose.translationMat, robotPose.rotationMat.rotateBy(Rotation.fromDegrees(180)));
		}
		
		if (isDone(robotPose)) {
			return new OrangeDrive.DriveVelocity(0, 0);
		}
		double radius = getRadius(robotPose, lookAheadDistance);
		if(radius != 0){
			if(isReversed){
				return new OrangeDrive.DriveVelocity(-robotSpeed, -robotDiameter * (robotSpeed / radius) / 1.5);
			} else {
				return new OrangeDrive.DriveVelocity(robotSpeed, robotDiameter * (robotSpeed / radius) / 1.5);				
			}
		} else {
			if(isReversed){
				return new OrangeDrive.DriveVelocity(-robotSpeed, 0);
			} else {
				return new OrangeDrive.DriveVelocity(robotSpeed, 0);				
			}
		}

		// TODO: Lower wheel speed as you get closer to endpoint
		// get distance to next point and subtract distance
		// if it is positive past the endpoint slow down
		// why does this work?????
		// robotdiameter * (speed / radius) / 2
		// ^ wheel speed delta

		// TODO:
		// scale to max speed
	}

	// Move lookaheaddistance to path
	public double getRadius(RigidTransform robotPose, double lookAheadDistance) {
		// Get point if robot was centered on 0 degrees
		Translation lookAheadPoint = robotPath.getLookAheadPoint(robotPose.translationMat, lookAheadDistance); //.rotateBy(robotPosition.rotationMat.inverse()); Don't rotate because it is done in getting the lookAheadPoint
	//	System.out.println("position " + lookAheadPoint.getX() + " " + lookAheadPoint.getY());
		// check if it is straight ahead or not
		// TODO: fix method of checking whether to drive straight or not
		// TODO: Constants
		Translation lookAheadPointToRobot = robotPose.translationMat.inverse().translateBy(lookAheadPoint);
		
		if (lookAheadPointToRobot.getX() * robotPose.rotationMat.sin() - lookAheadPointToRobot.getY() * robotPose.rotationMat.cos() < 0) {
			return 0;
		}
		double radius = Math.pow(lookAheadPoint.getDistanceTo(robotPose.translationMat), 2) / (2 * Math.abs(lookAheadPointToRobot.getX()));
		if (lookAheadPointToRobot.getX() > 0) {
			return radius;
		} else {
			return -radius;
		}
	}

	public boolean isDone(RigidTransform robotState) {
		// TODO: separate to translation and rotational isDone
		// TODO: Constants
		if (robotState.translationMat.getDistanceTo(robotPath.endPoint()) < 5) {
			return true;
		} else {
			return false;
		}
	}
}
