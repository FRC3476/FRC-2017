package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.utility.CircularQueue;
import org.usfirst.frc.team3476.utility.InterpolableValue;
import org.usfirst.frc.team3476.utility.RigidTransform;
import org.usfirst.frc.team3476.utility.Rotation;
import org.usfirst.frc.team3476.utility.Threaded;
import org.usfirst.frc.team3476.utility.Translation;

public class RobotTracker extends Threaded {

	private static final RobotTracker trackingInstance = new RobotTracker();

	public static RobotTracker getInstance() {
		return RobotTracker.trackingInstance;
	}

	private OrangeDrive driveBase;
	private RigidTransform currentOdometry;
	private CircularQueue<RigidTransform> vehicleHistory;

	private CircularQueue<Rotation> turretHistory;

	private double currentDistance, oldDistance, deltaDistance;

	private RobotTracker() {
		vehicleHistory = new CircularQueue<>(100);
		turretHistory = new CircularQueue<>(100);
		driveBase = OrangeDrive.getInstance();
		driveBase.zeroSensors();
		currentOdometry = new RigidTransform(new Translation(), driveBase.getGyroAngle());
	}

	public Rotation getGyroAngle(long time) {
		return vehicleHistory.getKey(time).rotationMat;
	}

	public synchronized RigidTransform getOdometry() {
		return currentOdometry;
	}

	public Rotation getTurretAngle(long time) {
		return turretHistory.getKey(time);
	}

	public synchronized void resetOdometry() {
		driveBase.zeroSensors();
		currentOdometry = new RigidTransform(new Translation(), driveBase.getGyroAngle());
		oldDistance = 0;
	}

	@Override
	public void update() {
		currentDistance = (driveBase.getLeftDistance() + driveBase.getRightDistance()) / 2;
		deltaDistance = currentDistance - oldDistance;
		Rotation deltaRotation = currentOdometry.rotationMat.inverse().rotateBy(driveBase.getGyroAngle());
		double sTBT;
		double cTBT;
		if (Math.abs(deltaRotation.getRadians()) < 1E-9) {
			sTBT = 1.0 - 1.0 / 6.0 * deltaRotation.getRadians() * deltaRotation.getRadians();
			cTBT = 0.5 * deltaRotation.getRadians() - 1.0 / 24.0 * Math.pow(deltaRotation.getRadians(), 3);
		} else {
			sTBT = deltaRotation.sin() / deltaRotation.getRadians();
			cTBT = (1 - deltaRotation.cos()) / deltaRotation.getRadians();
		}
		Translation deltaPosition = new Translation(cTBT * deltaDistance, sTBT * deltaDistance);
		synchronized (this) {
			currentOdometry = currentOdometry.transform(new RigidTransform(deltaPosition, deltaRotation));
			oldDistance = currentDistance;
		}
		vehicleHistory.add(new InterpolableValue<>(System.nanoTime(), currentOdometry));
		turretHistory.add(new InterpolableValue<>(System.nanoTime(), Shooter.getInstance().getAngle()));
	}
}

/*
 * How we calculate curvature
 * 
 * From https://github.com/strasdat/Sophus/blob/master/sophus/se2.hpp //Group
 * exponential
 * 
 */