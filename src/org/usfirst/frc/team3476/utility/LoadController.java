package org.usfirst.frc.team3476.utility;

public class LoadController {

	private double loadAccum;
	private double loadIncrease;
	private double decayRate;
	private double baseF;

	private boolean shoot = false;
	private boolean lastShoot = false;

	public LoadController(double baseF, double loadIncrease, double decayRate) {
		this.baseF = baseF;
		this.loadIncrease = loadIncrease;
		this.decayRate = decayRate;
	}

	public double calculate(boolean input, double setpoint) {
		lastShoot = shoot;
		// look for rising edge
		shoot = input;
		// decay before
		loadAccum = loadAccum * decayRate;

		if (shoot && !lastShoot) {
			loadAccum += loadIncrease;
		}

		double correctedOutput = baseF * setpoint + loadAccum;
		if (setpoint == 0) {
			return 0;
		}
		return correctedOutput / setpoint;
	}

	public void setBaseF(double baseF) {
		this.baseF = baseF;
	}

	public void setDecayRate(double decayRate) {
		this.decayRate = decayRate;
	}

	public void setLoadIncrease(double loadIncrease) {
		this.loadIncrease = loadIncrease;
	}

	public double getBaseF() {
		return baseF;
	}

	public double setDecayRate() {
		return decayRate;
	}

	public double setLoadIncrease() {
		return loadIncrease;
	}
}
