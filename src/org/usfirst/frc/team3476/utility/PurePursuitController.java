package org.usfirst.frc.team3476.utility;

import org.usfirst.frc.team3476.robot.Constants;
import org.usfirst.frc.team3476.subsystem.OrangeDrive;

public class PurePursuitController {
	/*
	 * 1. Translation delta compared to robot 2. Check x offset * angle to see
	 * if past tolerance 3. Create circle 4. Follow circle path 5. Actual path
	 * looks like a spline
	 */

	private Path robotPath;
	private boolean isReversed;

	public PurePursuitController(Path robotPath, boolean isReversed) {
		this.robotPath = robotPath;
		this.isReversed = isReversed;

	}

	public OrangeDrive.DriveVelocity calculate(RigidTransform robotPose) {

		if (isReversed) {
			robotPose = new RigidTransform(robotPose.translationMat,
					robotPose.rotationMat.rotateBy(Rotation.fromDegrees(180)));
		}

		if (isDone(robotPose)) {
			return new OrangeDrive.DriveVelocity(0, 0);
		}

		double radius = getRadius(robotPose, Constants.LookAheadDistance);// +
																			// robotPath.update(robotPose.translationMat));
		double robotSpeed = robotPath.getPathSpeed();

		if (isReversed) {
			robotSpeed *= -1;
		}

		if (radius != 0) {
			return new OrangeDrive.DriveVelocity(robotSpeed,
					Constants.DriveBaseDiameter * (robotSpeed / radius) / (2 * Constants.WheelScrub));
		} else {
			return new OrangeDrive.DriveVelocity(robotSpeed, 0);
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
		Translation lookAheadPoint = robotPath.getLookAheadPoint(robotPose.translationMat, lookAheadDistance); // .rotateBy(robotPosition.rotationMat.inverse());
																												// Don't
																												// rotate
																												// because
																												// it
																												// is
																												// done
																												// in
																												// getting
																												// the
																												// lookAheadPoint
		// System.out.println("position " + lookAheadPoint.getX() + " " +
		// lookAheadPoint.getY());
		// check if it is straight ahead or not
		// TODO: fix method of checking whether to drive straight or not
		// TODO: Constants
		Translation lookAheadPointToRobot = robotPose.translationMat.inverse().translateBy(lookAheadPoint);

		if (lookAheadPointToRobot.getX() * robotPose.rotationMat.sin()
				- lookAheadPointToRobot.getY() * robotPose.rotationMat.cos() < 0) {
			return 0;
		}
		double radius = Math.pow(lookAheadPoint.getDistanceTo(robotPose.translationMat), 2)
				/ (2 * Math.abs(lookAheadPointToRobot.getX()));
		if (lookAheadPointToRobot.getX() > 0) {
			return radius;
		} else {
			return -radius;
		}
	}

	public boolean isDone(RigidTransform robotState) {
		// TODO: separate to translation and rotational isDone
		// TODO: Constants
		if (robotState.translationMat.getDistanceTo(robotPath.endPoint()) < 10) {
			return true;
		} else {
			return false;
		}
	}
}
