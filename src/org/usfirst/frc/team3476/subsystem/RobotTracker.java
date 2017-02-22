package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.utility.RigidTransform;
import org.usfirst.frc.team3476.utility.Rotation;
import org.usfirst.frc.team3476.utility.Threaded;
import org.usfirst.frc.team3476.utility.Translation;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SPI.Port;

public class RobotTracker extends Threaded {

	private static RobotTracker trackingInstance = new RobotTracker();
	private OrangeDrive driveBase = OrangeDrive.getInstance();
	private ADXRS450_Gyro gyroSensor = new ADXRS450_Gyro(SPI.Port.kOnboardCS0);

	private RigidTransform latestState;
	private Translation deltaPosition;
	private Rotation deltaRotation;

	private double currentDistance, oldDistance;

	public static RobotTracker getInstance() {
		System.out.println("tracking instance: " + trackingInstance);
		return trackingInstance;
	}

	private RobotTracker() {
		RUNNINGSPEED = 10;
		latestState = new RigidTransform(new Translation(), new Rotation());
	}

	// TODO: Optimize this
	@Override
	public void update() {
		// Average distance
		currentDistance = (driveBase.getLeftDistance() - driveBase.getRightDistance()) / 2;
		// Get change in rotation
		deltaRotation = latestState.rotationMat.inverse().rotateBy(new Rotation(Math.cos(gyroSensor.getAngle()), Math.sin(gyroSensor.getAngle())));
		// Get change in distance
		deltaPosition = new Translation(oldDistance - currentDistance, 0).rotateBy(deltaRotation);
		// transform the change to compared to the robot's current
		// position/rotation
		latestState.transform(new RigidTransform(deltaPosition, deltaRotation));
		// store old distance
		oldDistance = currentDistance;
	}

	public RigidTransform getCurrentPosition() {
		return latestState;
	}

}
