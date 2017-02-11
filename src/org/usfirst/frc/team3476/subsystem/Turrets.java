package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.utility.Dashcomm;
import org.usfirst.frc.team3476.utility.Rotation;
import org.usfirst.frc.team3476.utility.Threaded;

import com.ctre.CANTalon;
import com.ctre.CANTalon.StatusFrameRate;

public class Turrets {

	private double tolerance;
	
	
	private Rotation setAngle;
	private CANTalon leftTalon;
	private CANTalon rightTalon;
	
	public Turrets(int leftTurretTalonId, int rightTurretTalonId) {
		leftTalon = new CANTalon(leftTurretTalonId);
		rightTalon = new CANTalon(rightTurretTalonId);
		
		leftTalon.enableBrakeMode(true);
		rightTalon.enableBrakeMode(true);
		
		leftTalon.setStatusFrameRateMs(StatusFrameRate.QuadEncoder, 10);
		rightTalon.setStatusFrameRateMs(StatusFrameRate.QuadEncoder, 10);
		
		// set PID
	}

	private void setAngle(Rotation setAngle){
		// setsetpoint 
		// rotations per degree
		// :( add special case for turning 90
		
		this.setAngle = setAngle;
	}
	
	private Rotation getAngle(){
		return setAngle;
	}
	
	public boolean isDone(){
		
	}
	
	public void setTolerance(double tolerance){
		this.tolerance = tolerance;
	}
	
}


























