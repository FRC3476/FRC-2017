package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.utility.RigidTransform;
import org.usfirst.frc.team3476.utility.Rotation;
import org.usfirst.frc.team3476.utility.Threaded;
import org.usfirst.frc.team3476.utility.Translation;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.SPI;

public class RobotTracker extends Threaded {

	private static final RobotTracker trackingInstance = new RobotTracker();
	private OrangeDrive driveBase = OrangeDrive.getInstance();
	private ADXRS450_Gyro gyroSensor = new ADXRS450_Gyro(SPI.Port.kOnboardCS0);

	private RigidTransform latestState;
	private Translation deltaPosition;
	private Rotation deltaRotation;

	private double currentDistance, oldDistance;

	public static RobotTracker getInstance() {
		return trackingInstance;
	}

	private RobotTracker() {
		RUNNINGSPEED = 10;
		latestState = new RigidTransform(new Translation(), new Rotation());
	}

	// TODO: Optimize this
	@Override
	public synchronized void update() {
		// Average distance
		currentDistance = (driveBase.getLeftDistance() - driveBase.getRightDistance()) / 2;
		// Get change in rotation
		deltaRotation = latestState.rotationMat.inverse().rotateBy(new Rotation(Math.cos(gyroSensor.getAngle()), Math.sin(gyroSensor.getAngle())));
		// Get change in distance
		deltaPosition = new Translation(oldDistance - currentDistance, 0).rotateBy(deltaRotation);
		// transform the change to compared to the robot's current
		// position/rotation
		// 1E-10 is arbitrary. It is a tolerance
		if(Math.abs(deltaRotation.getRadians()) < 1E-10){
			
		} else {
			
		}
		latestState.transform(new RigidTransform(deltaPosition, deltaRotation));
		// store old distance
		oldDistance = currentDistance;
	}

	public synchronized RigidTransform getCurrentPosition() {
		return latestState;
	}
}

/*
TODO: 
 
How we calculate curvature

From https://github.com/strasdat/Sophus/blob/master/sophus/se2.hpp
//Group exponential
//
// This functions takes in an element of tangent space (= twist ``a``) and
// returns the corresponding element of the group SE(2).
//
// The first two components of ``a`` represent the translational part
// ``upsilon`` in the tangent space of SE(2), while the last three components
// of ``a`` represents the rotation vector ``omega``.
// To be more specific, this function computes ``expmat(hat(a))`` with
// ``expmat(.)`` being the matrix exponential and ``hat(.)`` the hat-operator
// of SE(2), see below.
//
SOPHUS_FUNC static SE2<Scalar> exp(Tangent const& a) {
 Scalar theta = a[2];
 SO2<Scalar> so2 = SO2<Scalar>::exp(theta);
 Scalar sin_theta_by_theta;
 Scalar one_minus_cos_theta_by_theta;

 if (std::abs(theta) < Constants<Scalar>::epsilon()) {
   Scalar theta_sq = theta * theta;
   sin_theta_by_theta = Scalar(1.) - Scalar(1. / 6.) * theta_sq;
   one_minus_cos_theta_by_theta =
       Scalar(0.5) * theta - Scalar(1. / 24.) * theta * theta_sq;
 } else {
   sin_theta_by_theta = so2.unit_complex().y() / theta;
   one_minus_cos_theta_by_theta =
       (Scalar(1.) - so2.unit_complex().x()) / theta;
 }
 Vector2<Scalar> trans(
     sin_theta_by_theta * a[0] - one_minus_cos_theta_by_theta * a[1],
     one_minus_cos_theta_by_theta * a[0] + sin_theta_by_theta * a[1]);
 return SE2<Scalar>(so2, trans);
}

*/