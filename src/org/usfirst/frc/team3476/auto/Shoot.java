package org.usfirst.frc.team3476.auto;

import org.usfirst.frc.team3476.subsystem.Shooter;
import org.usfirst.frc.team3476.subsystem.Shooter.ShooterState;

public class Shoot implements Action {
	private Shooter shooter = Shooter.getInstance();
	private double speed;
	
	public Shoot(double speed) {
		this.speed = speed;
	}

	@Override
	public void start() {
		shooter.setSpeed(speed);
		shooter.setState(ShooterState.SHOOT);
	}

	@Override
	public boolean isDone() {
		return true;
	}

}
