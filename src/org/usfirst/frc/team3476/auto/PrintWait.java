package org.usfirst.frc.team3476.auto;

public class PrintWait implements Action {
	private String msg;
	private double delay;
	private double startTime;

	public PrintWait(String msg, int delay) {
		this.msg = msg;
		this.delay = delay;
	}

	@Override
	public boolean isDone() {
		if (System.currentTimeMillis() - startTime < delay) {
			return false;
		}
		return true;
	}

	@Override
	public void start() {
		System.out.println(msg);
		startTime = System.currentTimeMillis();
	}
}
