package org.usfirst.frc.team3476.utility;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

public class LPController {
	
	CANTalon masterShooter, slaveShooter;
	double loadFactor = 0;
	double setpoint = 0;
	double loadIncrease = 0;
	double decayRate = 0;
	double baseF = 0.035;
	
	boolean shoot = false;
	boolean lastShoot = false;
	boolean isRunning = false;
	
	public LPController(){
		masterShooter = new CANTalon(4);
		slaveShooter.changeControlMode(TalonControlMode.Follower);
		slaveShooter.set(4);
		
		masterShooter.enable();
		masterShooter.setSetpoint(setpoint);
	}
	
	public double calculate(boolean input){
		masterShooter.setSetpoint(setpoint);		
		lastShoot = shoot;
		// look for rising edge
		shoot = input || isRunning;
		if(shoot && !lastShoot) {
			loadFactor+=loadIncrease;
		}
		//load factor decays just like newtons law of cooling
		loadFactor = loadFactor*decayRate;
		
		//calculate the F such that we are simply adding the L factor through it.
		double F = calculateF(baseF,loadIncrease,loadFactor,masterShooter.getSetpoint());
		return F;
		
	}
	
	
	private double calculateF(double baseF, double L, double loadFactor, double setpoint){
		double contribution = setpoint*baseF; //this should be a value 0-1 or 0-12. It is the ceontribution of the F term.
		double newContrib = contribution+loadFactor;
		if(setpoint==0){
			System.out.println("divide by zero!");
			return 0;
		}else return newContrib/setpoint;
	}
	
	public void enable(){
		isRunning = true;
	}
}

