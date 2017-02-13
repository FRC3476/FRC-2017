package org.usfirst.frc.team3476.utility;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class Threaded implements Runnable {
	private ScheduledFuture<?> taskFuture;
	private boolean isRunning = false;

	protected int RUNNINGSPEED;

	public abstract void update();

	@Override
	public void run() {
		if (isRunning) {
			update();
		}
	}

	public void addTask(ScheduledExecutorService execIn) {
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

	public void setRunningState(boolean isRunning) {
		this.isRunning = isRunning;
	}
}
