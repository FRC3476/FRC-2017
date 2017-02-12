package org.usfirst.frc.team3476.auto;

import org.usfirst.frc.team3476.subsystem.OrangeDrive;
import org.usfirst.frc.team3476.utility.Path;

public class Drive implements Action{
	Path setPath;
	
	public Drive(Path path) {
		setPath = path;
	}
	
	@Override
	public void start() {
		OrangeDrive.getInstance().setAutoPath(setPath);
		
	}

	@Override
	public boolean isDone() {
		return OrangeDrive.getInstance().isDone();
	}

}
