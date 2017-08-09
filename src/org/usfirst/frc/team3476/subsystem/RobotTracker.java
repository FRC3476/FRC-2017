package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.utility.CircularQueue;
import org.usfirst.frc.team3476.utility.RigidTransform;
import org.usfirst.frc.team3476.utility.Rotation;
import org.usfirst.frc.team3476.utility.Threaded;
import org.usfirst.frc.team3476.utility.InterpolableValue;
import org.usfirst.frc.team3476.utility.Translation;

public class RobotTracker extends Threaded {

	private static final RobotTracker trackingInstance = new RobotTracker();
	private OrangeDrive driveBase;

	private RigidTransform currentOdometry;
	private CircularQueue<RigidTransform> vehicleHistory;
	private CircularQueue<Rotation> turretHistory;
	
	private double currentDistance, oldDistance, deltaDistance;	
	
	public static RobotTracker getInstance() {
		return trackingInstance;
	}

	private RobotTracker() {
		vehicleHistory = new CircularQueue<RigidTransform>(100);
		turretHistory = new CircularQueue<Rotation>(100);
		driveBase = OrangeDrive.getInstance();
		driveBase.zeroSensors();
		currentOdometry = new RigidTransform(new Translation(), driveBase.getGyroAngle());
	}

	@Override
	public void update() {
		currentDistance = (driveBase.getLeftDistance() + driveBase.getRightDistance()) / 2;
		deltaDistance = currentDistance - oldDistance;
		Rotation deltaRotation = currentOdometry.rotationMat.inverse().rotateBy(driveBase.getGyroAngle());
		double sTBT;
		double cTBT;
		if(Math.abs(deltaRotation.getRadians()) < 1E-9){
			sTBT = 1.0 - 1.0 / 6.0 * deltaRotation.getRadians() * deltaRotation.getRadians();
			cTBT = 0.5 * deltaRotation.getRadians() - 1.0 / 24.0 * Math.pow(deltaRotation.getRadians(), 3);
		} else {
			sTBT = deltaRotation.sin() / deltaRotation.getRadians();
			cTBT = (1 - deltaRotation.cos()) / deltaRotation.getRadians();
		}
		Translation deltaPosition = new Translation(cTBT * deltaDistance, sTBT * deltaDistance);
		synchronized(this){
			currentOdometry = currentOdometry.transform(new RigidTransform(deltaPosition, deltaRotation));
			oldDistance = currentDistance;
		}
		vehicleHistory.add(new InterpolableValue<RigidTransform>(System.nanoTime(), currentOdometry));
		turretHistory.add(new InterpolableValue<Rotation>(System.nanoTime(), Shooter.getInstance().getAngle()));
	}
	
	public Rotation getTurretAngle(long time){
		return turretHistory.getKey(time);
	}
	
	public Rotation getGyroAngle(long time){
		return vehicleHistory.getKey(time).rotationMat;
	}
	
	public synchronized RigidTransform getOdometry() {
		return currentOdometry;
	}
	
	public synchronized void resetOdometry(){
		driveBase.zeroSensors();
		currentOdometry = new RigidTransform(new Translation(), driveBase.getGyroAngle());
		oldDistance = 0;
	}
}

/* 
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