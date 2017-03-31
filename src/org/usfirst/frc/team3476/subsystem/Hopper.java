package org.usfirst.frc.team3476.subsystem;

import com.ctre.CANTalon;

public class Hopper {

	private static final Hopper hopperInstance = new Hopper();
	
	private CANTalon turretFeeder;
	private CANTalon blenderMotor;
	private CANTalon motivatorWheel;
	
	
	public static Hopper getInstance(){
		return hopperInstance;
	}
	
	private Hopper(){
		turretFeeder = new CANTalon(6);
		blenderMotor = new CANTalon(7);
		motivatorWheel = new CANTalon(11);
	}
	
	public void setRun(boolean isRunning){
		if(isRunning){
			turretFeeder.set(-1);
			blenderMotor.set(-0.45);
			motivatorWheel.set(-0.6);
		} else {
			turretFeeder.set(0);
			blenderMotor.set(0);
			motivatorWheel.set(0);			
		}
	}
	
}
