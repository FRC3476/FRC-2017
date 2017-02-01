package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.utility.LPController;
import org.usfirst.frc.team3476.utility.Threaded;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;

public class Flywheel extends Threaded {
	
	CANTalon masterTalon, slaveTalon;
	
	private double toleranceRange = 50;
	private double batVolt;
	private double motorOutput;
	private double setpoint;
	
	LPController specialController = new LPController(0.035, 0.005, 0.3);
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
		masterTalon.configEncoderCodesPerRev(3072);
		/*
		masterTalon.setP(0.28);
		masterTalon.setI(0);
		masterTalon.setD(7);
		masterTalon.setF(0.0125);
		Constants TBD
		*/		
	}
	
	public void update(){
		motorOutput = specialController.calculate(ballSensor.get(), setpoint);
		masterTalon.set(motorOutput);
	}
	
	public void setSetpoint(double setpoint){
		this.setpoint = setpoint;
	}
	
	public void setTolerance(double toleranceRange){
		this.toleranceRange = toleranceRange;
	}
	
	public boolean isAtSpeed(){
		return Math.abs(getSetpoint() - masterTalon.getSpeed()) < toleranceRange;
	}
	
	public double getRpm(){
		return masterTalon.getSpeed();
	}
	
	public double getSetpoint(){
		return setpoint;
	}
	
	public double getOutput(){
		return motorOutput;
	}
}
