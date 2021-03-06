package org.usfirst.frc.team3476.auto;

import org.usfirst.frc.team3476.subsystem.OrangeDrive;
import org.usfirst.frc.team3476.subsystem.RobotTracker;
import org.usfirst.frc.team3476.utility.Path;
import org.usfirst.frc.team3476.utility.Path.Waypoint;

public class Drive implements Action {
	Path setPath;
	boolean reversed;

	/*
	 * public Drive(Path path) { setPath = path; }
	 */
	public Drive(double x, double y, double speed, boolean reversed) {
		setPath = new Path(new Waypoint(x, y, speed));
		this.reversed = reversed;
	}

	@Override
	public boolean isDone() {
		return OrangeDrive.getInstance().isDone();
	}

	@Override
	public void start() {
		RobotTracker.getInstance().resetOdometry();
		OrangeDrive.getInstance().setAutoPath(setPath, reversed);
	}

}
