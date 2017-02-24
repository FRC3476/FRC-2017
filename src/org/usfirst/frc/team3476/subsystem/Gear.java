package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.utility.Constants;
import org.usfirst.frc.team3476.utility.Threaded;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;

public class Gear extends Threaded {

	private Solenoid gearMech;
	private static Gear gearInstance = new Gear();
	private DigitalInput pegSensor;

	// Maybe check air pressure
	// Is this subsystem useless? We only need one or two pneumatic

	public static Gear getInstance() {
		return gearInstance;
	}

	private Gear() {
		RUNNINGSPEED = 50;
		gearMech = new Solenoid(Constants.GearSolenoidId);
		pegSensor = new DigitalInput(Constants.PegSensorId);
	}

	public void setGearMech(boolean pushed) {
		gearMech.set(pushed);
	}
	
	@Override
	public void update(){
		if(pegSensor.get()){
			setGearMech(false);
		} else {
			setGearMech(true);
		}
	}
	
	public boolean isPushed(){
		return gearMech.get();
	}

	/*
	 * if banner sensor
	 * public void getGearInPlace(){
	 * check if gear is there
	 * return if gear is there
	 * }
	 */
}
