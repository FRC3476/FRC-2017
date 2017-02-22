package org.usfirst.frc.team3476.auto;

import org.usfirst.frc.team3476.subsystem.OrangeDrive;
import org.usfirst.frc.team3476.utility.Path;
import org.usfirst.frc.team3476.utility.Path.Waypoint;

public class Drive implements Action {
	Path setPath;
	OrangeDrive drive = OrangeDrive.getInstance();

	/*public Drive(Path path) {
		setPath = path;
	}*/
	public Drive(double x, double y)
	{
		setPath = new Path(new Waypoint(x,y,10));
	}

	@Override
	public void start() {
		drive.setAutoPath(setPath);

	}

	@Override
	public boolean isDone() {
		return drive.isDone();
	}

}
