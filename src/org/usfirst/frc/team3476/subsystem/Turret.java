package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.utility.Constants;
import org.usfirst.frc.team3476.utility.Rotation;

import com.ctre.CANTalon;
import com.ctre.CANTalon.StatusFrameRate;
import com.ctre.CANTalon.TalonControlMode;

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

	// Discuss doing everything in radians
	public void setAngle(Rotation setAngle) {
		// setsetpoint
		// rotations per degree
		// :( add special case for turning 90
		turretTalon.setSetpoint((setAngle.getDegrees() / 360) * Constants.TurretTicksPerRotations);
		this.setAngle = setAngle;
	}

	public Rotation getAngle() {
		return new Rotation(turretTalon.getPosition() / Constants.TurretTicksPerRotations);
	}

	public Rotation getSetpoint() {
		return setAngle;
	}

	/*
	 * public boolean isDone(){
	 * }
	 */

	public void setTolerance(double tolerance) {

		this.tolerance = tolerance;
	}
	
	
	//THIS IS FOR TESTING ONLY -- DELETE AFTERWARDS
	public void setManual(double power)
	{
		turretTalon.changeControlMode(TalonControlMode.PercentVbus);
		turretTalon.setSetpoint(power);
	}

}
