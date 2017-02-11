package org.usfirst.frc.team3476.subsystem;

import edu.wpi.first.wpilibj.Solenoid;

public class Gear {
	
	private Solenoid gearMech = new Solenoid(1);
	private static Gear gearInstance = new Gear();
	
	// Maybe check air pressure
	// Is this subsystem useless? We only need one or two pneumatic
	
	public static Gear getInstance(){
		return gearInstance;
	}
	
	private Gear(){
		// TODO: add stuffs here
	}
	
	public void setGearMech(boolean pushed){
		gearMech.set(pushed);
	}
	
	
	/* if banner sensor
	public void getGearInPlace(){
		check if gear is there 
		return if gear is there
	}
	*/
}
