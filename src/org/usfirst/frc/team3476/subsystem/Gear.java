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
	private Solenoid gearFlap;
	private double gearStartTime;

	public static Gear getInstance() {
		return gearInstance;
	}

	private Gear() {
		RUNNINGSPEED = 5;
		gearMech = new Solenoid(Constants.GearSolenoidId);
		pegSensor = new DigitalInput(Constants.PegSensorId);
		gearFlap = new Solenoid(Constants.GearFlapSolenoidId);
		gearStartTime = System.currentTimeMillis();
	}

	public void setGearMech(boolean pushed) {
		gearMech.set(pushed);
		//System.out.println("pushed: " + isPushed());
	}
	
	@Override
	public void update(){
		
		if(!pegSensor.get()){
			gearStartTime= System.currentTimeMillis();
		}
		
		if(System.currentTimeMillis() - gearStartTime < 1000){
			setGearMech(true);
		} else {
			setGearMech(false);
		}
		
	}
	
	public boolean isPushed(){
		return !pegSensor.get();
	}
	
	public void toggleFlap(){
		if(gearFlap.get()){
			gearFlap.set(false);
		} else {
			gearFlap.set(true);
		}
	}
	
	/*
	 * if banner sensor
	 * public void getGearInPlace(){
	 * check if gear is there
	 * return if gear is there
	 * }
	 */
}
