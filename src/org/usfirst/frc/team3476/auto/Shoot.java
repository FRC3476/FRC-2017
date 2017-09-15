package org.usfirst.frc.team3476.auto;

import org.usfirst.frc.team3476.subsystem.Shooter;
import org.usfirst.frc.team3476.subsystem.Shooter.ShooterState;

public class Shoot implements Action {
	private Shooter shooter = Shooter.getInstance();

	public Shoot() {
	}

	@Override
	public boolean isDone() {
		return true;
	}

	@Override
	public void start() {
		shooter.setState(ShooterState.SHOOT);
	}

}
