package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.utility.LPController;
import org.usfirst.frc.team3476.utility.Threaded;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;

public class Flywheel extends Threaded {
	
	CANTalon masterTalon, slaveTalon;
	double setpoint;
	double toleranceRange = 50;
	double batVolt;
	
	LPController specialController = new LPController();
	DigitalInput ballSensor = new DigitalInput(1);
	
	public Flywheel(int masterTalonId, int slaveTalonId){
		RUNNINGSPEED = 10;
		masterTalon = new CANTalon(masterTalonId);
		slaveTalon = new CANTalon(slaveTalonId);
		slaveTalon.changeControlMode(TalonControlMode.Follower);
		slaveTalon.set(masterTalonId);		
		
		masterTalon.enableBrakeMode(false);
		slaveTalon.enableBrakeMode(false);
		
		masterTalon.clearStickyFaults();
		slaveTalon.clearStickyFaults();
		
		// TODO: Voltage Compensation (Probably change feedforward)
		batVolt = DriverStation.getInstance().getBatteryVoltage();
		
		masterTalon.changeControlMode(TalonControlMode.PercentVbus);
		/*
		masterTalon.setP(0.28);
		masterTalon.setI(0);
		masterTalon.setD(7);
		masterTalon.setF(0.0125);
		Constants TBD
		*/		
	}
	
	public void update(){
		masterTalon.setF(specialController.calculate(ballSensor.get()));
	}
	
	public void setSetpoint(double setpoint){
		this.setpoint = setpoint;
		masterTalon.setSetpoint(setpoint);
	}
	
	public void setTolerance(double toleranceRange){
		this.toleranceRange = toleranceRange;
	}
	
	public boolean isAtSpeed(){
		return Math.abs(setpoint - masterTalon.getSpeed()) < toleranceRange;
	}
	
	public double getRpm(){
		return masterTalon.getSpeed();
	}
	
	public double getSetpoint(){
		return setpoint;
	}
	
}
