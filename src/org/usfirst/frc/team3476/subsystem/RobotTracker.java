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
		driveBase.zeroSensors();
	}

	// TODO: Optimize this
	@Override
	public synchronized void update() {
		// Average distance
		currentDistance = (driveBase.getLeftDistance() + driveBase.getRightDistance()) / 2;
		// Get change in rotation
		deltaRotation = latestState.rotationMat.inverse().rotateBy(driveBase.getGyroAngle());
		// Get change in distance
		Translation deltaPosition;
		// transform the change to compared to the robot's current
		// position/rotation
		// 1E-10 is arbitrary. It is a tolerance
		double sTBT;
		double cTBT;
		if(Math.abs(deltaRotation.getRadians()) < 1E-10){
			sTBT = 1.0 - 1.0 / 6.0 * deltaRotation.getRadians() * deltaRotation.getRadians();
			cTBT = 0.5 * deltaRotation.getRadians() - 1.0 / 24.0 * Math.pow(deltaRotation.getRadians(), 3);
		} else {
			sTBT = deltaRotation.sin() / deltaRotation.getRadians();
			cTBT = (1 - deltaRotation.cos()) / deltaRotation.getRadians();
		}

		deltaPosition = new Translation(sTBT * (currentDistance-oldDistance), cTBT * (currentDistance-oldDistance));
		latestState.transform(new RigidTransform(deltaPosition, deltaRotation));
		// store old distance
		System.out.println(sTBT + cTBT);
		System.out.println(latestState.translationMat.getX() + "  " + latestState.translationMat.getY());
		oldDistance = currentDistance;
	}

	public synchronized RigidTransform getCurrentPosition() {
		return latestState;
	}
	
	public synchronized Rotation getCurrentAngle(){
		return latestState.rotationMat;
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