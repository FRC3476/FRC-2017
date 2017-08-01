package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.utility.Constants;
import org.usfirst.frc.team3476.utility.Rotation;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

public class Turret {

	private double tolerance;
	private CANTalon turretTalon;
	//P: .12
	//I: .002
	//I-ZONE: 400
	//Don't delete this
	public Turret(int turretTalonId) {
		
		turretTalon = new CANTalon(turretTalonId);

		turretTalon.enableBrakeMode(true);
		turretTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		turretTalon.reverseSensor(true);
		turretTalon.configPeakOutputVoltage(4.8, -4.8);
		turretTalon.setPID(1.15, 0, 0.5);
		turretTalon.setPosition(0);
		tolerance = 0.5;
		//1024 * (140/24)
	}

	public void setAngle(Rotation setAngle) {
		turretTalon.changeControlMode(TalonControlMode.Position);
		if(setAngle.getDegrees() < 120 && setAngle.getDegrees() > -120){
			turretTalon.setSetpoint(angleToTicks(setAngle));
		}
	}

	public double angleToTicks(Rotation setAngle){
		return (setAngle.getRadians() / (2 * Math.PI)) * Constants.TurretTicksPerRotations;
	}
	
	public Rotation getAngle() {
		return Rotation.fromRadians((turretTalon.getPosition() / Constants.TurretTicksPerRotations) * 2 * Math.PI );
	}

	public Rotation getSetAngle() {
		return Rotation.fromRadians((turretTalon.getSetpoint() / Constants.TurretTicksPerRotations) * 2 * Math.PI );
	}
	
	public double getSetpoint(){
		return turretTalon.getSetpoint();
	}
	
	public synchronized boolean isDone(){
		return Math.abs(getAngle().getDegrees() - getSetAngle().getDegrees()) < tolerance;
	}	 

	public synchronized void setTolerance(double tolerance) {
		this.tolerance = tolerance;
	}	
	
	public void setManual(double power)	{
		turretTalon.changeControlMode(TalonControlMode.PercentVbus);
		turretTalon.setSetpoint(power);
	}
	
	public void resetPosition(){
		turretTalon.setPosition(angleToTicks(Rotation.fromDegrees(30)));
	}
}
