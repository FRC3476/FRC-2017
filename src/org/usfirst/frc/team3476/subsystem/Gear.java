package org.usfirst.frc.team3476.subsystem;

import org.omg.CORBA.SystemException;
import org.usfirst.frc.team3476.utility.Constants;
import org.usfirst.frc.team3476.utility.Threaded;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;

public class Gear extends Threaded {

	private Solenoid gearMech;
	private static final Gear gearInstance = new Gear();
	private DigitalInput pegSensor;


	public static Gear getInstance() {
		return gearInstance;
	}

	private Gear() {
		RUNNINGSPEED = 100;
		gearMech = new Solenoid(Constants.GearSolenoidId);
		pegSensor = new DigitalInput(Constants.PegSensorId);
	}

	public void setGearMech(boolean pushed) {
		gearMech.set(pushed);
		//System.out.println("pushed: " + isPushed());
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
		return !pegSensor.get();
	}

	/*
	 * if banner sensor
	 * public void getGearInPlace(){
	 * check if gear is there
	 * return if gear is there
	 * }
	 */
}
