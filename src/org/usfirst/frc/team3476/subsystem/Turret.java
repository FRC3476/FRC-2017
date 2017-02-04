package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.utility.Threaded;

public class Turret extends Threaded {
	
	public enum TurretState {
		IDLE, PASSIVE, ACTIVE, LOCKED
	}
	
	TurretState currentState;
	
	public Turret(int turretTalonId){
		RUNNINGSPEED = 5;
		currentState = TurretState.IDLE;
	}
	
	public void update(){
		switch(currentState){
		case IDLE:
			break;
		case PASSIVE:
			// Normal aim at goal first
			//
			break;
		case ACTIVE:
			// Search for goal if it isn't found
			break;
		case LOCKED:
			// After finding goal
			// Aim at it while moving
			break;
		}
	}
	
	public void passiveAim(){
		
	}
	
	public void activeAim(){
		
	}
	
	public void lockedAim(){
		// Might need calibration during match
	}
	
	
}
