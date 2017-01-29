package org.usfirst.frc.team3476.utility;

import java.util.ArrayList;

import com.ctre.CANTalon;

public class CANTalonChecker extends Threaded {

	ArrayList<CANTalon> motorArray;

	private int MAXCURRENT;
	// not sure about max current

	public CANTalonChecker(int MAX) {
		RUNNINGSPEED = 250;
		MAXCURRENT = MAX;
	}

	public void add(CANTalon motorIn) {
		motorArray.add(motorIn);
	}

	public void remove(CANTalon motorIn) {
		motorArray.remove(motorIn);
	}

	@Override
	public void update() {
		for (CANTalon motor : motorArray) {
			if (motor.getOutputCurrent() > MAXCURRENT) {
				motor.disableControl();
			}
		}

	}

}
