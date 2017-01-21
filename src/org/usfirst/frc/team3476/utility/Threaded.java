package org.usfirst.frc.team3476.utility;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class Threaded implements Runnable {
	private ScheduledFuture<?> taskFuture;
	
	protected int RUNNINGSPEED;

	@Override
	public abstract void run();

	public void startTask(ScheduledExecutorService execIn) {
		if (RUNNINGSPEED <= 0) { // int is zeroinitialized
			System.out.println("RUNNINGSPEED not initialized or is negative");
		} else {
			taskFuture = execIn.scheduleAtFixedRate(this, 0, RUNNINGSPEED, TimeUnit.MILLISECONDS);
		}
	}

	public void endTask() {
		if (taskFuture != null) {
			if (!taskFuture.isCancelled()) {
				taskFuture.cancel(false);
			}
		}
	}
}
