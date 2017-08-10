package org.usfirst.frc.team3476.auto;

import org.usfirst.frc.team3476.subsystem.OrangeDrive;
import org.usfirst.frc.team3476.utility.Rotation;

public class Rotate implements Action {
	OrangeDrive drive = OrangeDrive.getInstance();;
	Rotation angle;

	public Rotate(double angle) {
		this.angle = Rotation.fromDegrees(angle);
	}

	@Override
	public boolean isDone() {
		if (drive.isDone()) {
			return true;
		}
		return false;
	}

	@Override
	public void start() {
		drive.setRotation(angle);
	}

}
