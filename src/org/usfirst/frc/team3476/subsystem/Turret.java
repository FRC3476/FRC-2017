package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.utility.Rotation;

import com.ctre.CANTalon;
import com.ctre.CANTalon.StatusFrameRate;

public class Turret {

	private double tolerance;
	
	
	private Rotation setAngle;
	private CANTalon turretTalon;
	
	public Turret(int turretTalonId) {
		turretTalon = new CANTalon(turretTalonId);
		
		turretTalon.enableBrakeMode(true);
		
		turretTalon.setStatusFrameRateMs(StatusFrameRate.QuadEncoder, 10);
		
		// set PID
	}

	public void setAngle(Rotation setAngle){
		// setsetpoint 
		// rotations per degree
		// :( add special case for turning 90
		
		this.setAngle = setAngle;
	}
	
	public Rotation getAngle(){

	}
	
	public Rotation getSetpoint(){
		return setAngle;
	}
	
	/*
	public boolean isDone(){
		
	}
	*/
	
	public void setTolerance(double tolerance){
		this.tolerance = tolerance;
	}
	
}


























