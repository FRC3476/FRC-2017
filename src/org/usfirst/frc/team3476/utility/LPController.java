package org.usfirst.frc.team3476.utility;

public class LPController {
	
	private double loadAccum = 0;
	private double loadIncrease = 0;
	private double decayRate = 0;
	private double baseF = 0;
	
	private boolean shoot = false;
	private boolean lastShoot = false;
	
	public LPController(double baseF, double loadIncrease, double decayRate){
		
		this.baseF = baseF;
		this.loadIncrease = loadIncrease;
		this.decayRate = decayRate;
		
	}
	
	public double calculate(boolean input, double setpoint){		
		lastShoot = shoot;
		// look for rising edge
		shoot = input;
		// decay before
		loadAccum = loadAccum*decayRate;
		
		if(shoot && !lastShoot) {
			loadAccum += loadIncrease;
		}
		
		double correctedOutput = baseF * setpoint + loadAccum;
		return correctedOutput;		
	}
	
}

