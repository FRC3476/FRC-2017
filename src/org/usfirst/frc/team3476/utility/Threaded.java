package org.usfirst.frc.team3476.utility;

public abstract class Threaded implements Runnable {

	@Override
	public void run() {
		update();
		// TODO: update to log stuff
	}

	public abstract void update();
}
