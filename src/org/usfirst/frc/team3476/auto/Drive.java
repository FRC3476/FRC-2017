package org.usfirst.frc.team3476.auto;

import org.usfirst.frc.team3476.subsystem.OrangeDrive;
import org.usfirst.frc.team3476.utility.Path;

public class Drive implements Action{
	Path setPath;
	OrangeDrive drive = new OrangeDrive.getInstance();
	
	public Drive(Path path) {
		setPath = path;
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
