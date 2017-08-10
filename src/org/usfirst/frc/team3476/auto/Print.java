package org.usfirst.frc.team3476.auto;

public class Print implements Action {
	private String msg;

	public Print(String str) {
		msg = str;
	}

	@Override
	public boolean isDone() {
		return true;
	}

	@Override
	public void start() {
		System.out.println(msg);

	}

}
