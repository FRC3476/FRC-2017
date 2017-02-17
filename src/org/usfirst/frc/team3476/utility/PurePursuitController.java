package org.usfirst.frc.team3476.utility;

import org.usfirst.frc.team3476.subsystem.OrangeDrive;

public class PurePursuitController {
	/* 1. Translation delta compared to robot
	 * 2. Check x offset * angle to see if past tolerance
	 * 3. Create circle
	 * 4. Follow circle path */

	private double lookAheadDistance;
	private double robotSpeed;
	private double robotDiameter;
	private Path robotPath;

	public PurePursuitController(double lookAheadDistance, double robotSpeed, double robotDiameter, Path robotPath) {
		this.lookAheadDistance = lookAheadDistance;
		this.robotSpeed = robotSpeed;
		this.robotDiameter = robotDiameter;
		this.robotPath = robotPath;

	}

	public OrangeDrive.DriveVelocity calculate(RigidTransform robotState) {

		if (isDone(robotState)) {
			return new OrangeDrive.DriveVelocity(0, 0);
		}
		double radius = getRadius(robotState, lookAheadDistance);
		double deltaSpeed = robotDiameter * (robotSpeed / radius) / 2;
		
		// TODO: Lower wheel speed as you get closer to endpoint
		// get distance to next point and subtract distance
		// if it is positive past the endpoint slow down
		return new OrangeDrive.DriveVelocity(robotSpeed, deltaSpeed);
		// why does this work?????
		// robotdiameter * (speed / radius) / 2
		// ^ wheel speed delta

		// TODO:
		// scale to max speed
	}

	// Move lookaheaddistance to path
	public double getRadius(RigidTransform robotPosition, double lookAheadDistance) {
		// Get point if robot was centered on 0 degrees
		Translation lookAheadPoint = robotPath.getLookAheadPoint(robotPosition.translationMat, lookAheadDistance).rotateBy(robotPosition.rotationMat.inverse());
		System.out.println("position " + lookAheadPoint.getX() + " " + lookAheadPoint.getY());
		// check if it is straight ahead or not
		if (Math.abs(lookAheadPoint.getX() - robotPosition.translationMat.getX()) < 1) {
			return 0;
		}
		double radius = Math.pow(lookAheadPoint.getDistanceTo(robotPosition.translationMat), 2)
				/ (2 * lookAheadPoint.getX());
		if (lookAheadPoint.getX() > 0) {
			System.out.println("radius " + radius);
			return radius;
		} else {
			System.out.println("radius " + -radius);
			return -radius;
		}
	}

	public boolean isDone(RigidTransform robotState) {
		// separate to translation and rotational isDone
		if (robotState.translationMat.getDistanceTo(robotPath.endPoint()) < 5) {
			return true;
		} else {
			return false;
		}
	}
}
