package org.usfirst.frc.team3476.auto;

import org.usfirst.frc.team3476.subsystem.Shooter;

public class SetFlywheelSpeed implements Action
{
	private int speed;
	public SetFlywheelSpeed()
	{
		speed = 2000;
	}
	
	public SetFlywheelSpeed(int speed)
	{
		this.speed = speed;
	}

	@Override
	public void start()
	{
		Shooter.getInstance().setFlywheelSpeed(speed);
		Shooter.getInstance().enableFlywheel();
		System.out.println("starting flywheel");
	}

	@Override
	public boolean isDone()
	{
		System.out.println("Flywheel Done: " + Shooter.getInstance().isFlywheelDone());
		return Shooter.getInstance().isFlywheelDone();
	}

}
