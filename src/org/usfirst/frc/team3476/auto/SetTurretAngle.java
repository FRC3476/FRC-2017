package org.usfirst.frc.team3476.auto;

import org.usfirst.frc.team3476.subsystem.Shooter;
import org.usfirst.frc.team3476.utility.Rotation;

public class SetTurretAngle implements Action {

	private Rotation wantedRotation;

	public SetTurretAngle(double degrees) {
		wantedRotation = Rotation.fromDegrees(degrees);
	}

	@Override
	public boolean isDone() {
		return Shooter.getInstance().isDone();
	}

	@Override
	public void start() {
		Shooter.getInstance().setTurretAngle(wantedRotation);
	}

}
