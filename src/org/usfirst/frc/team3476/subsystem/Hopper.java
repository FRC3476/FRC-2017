package org.usfirst.frc.team3476.subsystem;

import com.ctre.CANTalon;

public class Hopper {

	public enum HopperState {
		RUNNING, STOPPED, BACKWARDS
	};

	private static final Hopper hopperInstance = new Hopper();

	public static Hopper getInstance() {
		return Hopper.hopperInstance;
	}

	private CANTalon turretFeeder;
	private CANTalon blenderMotor;

	private CANTalon motivatorWheel;

	private Hopper() {
		turretFeeder = new CANTalon(6);
		blenderMotor = new CANTalon(7);
		motivatorWheel = new CANTalon(11);

	}

	public double getCurrent() {
		return turretFeeder.getOutputCurrent();
	}

	public void setState(HopperState setState) {
		switch (setState) {
		case RUNNING:
			turretFeeder.set(-1);
			blenderMotor.set(-0.4);
			motivatorWheel.set(-0.6);
			break;
		case STOPPED:
			turretFeeder.set(0);
			blenderMotor.set(0);
			motivatorWheel.set(0);
			break;
		case BACKWARDS:
			turretFeeder.set(1);
			blenderMotor.set(0.55);
			motivatorWheel.set(0.8);
			break;
		}
	}

}
