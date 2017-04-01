package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.utility.Constants;
import org.usfirst.frc.team3476.utility.Dashcomm;
import org.usfirst.frc.team3476.utility.Interpolable;
import org.usfirst.frc.team3476.utility.Rotation;
import org.usfirst.frc.team3476.utility.SynchronousPid;
import org.usfirst.frc.team3476.utility.Threaded;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Shooter extends Threaded {
	public enum ShooterState {
		READY, SHOOT, IDLE
	}
	
	public enum TurretState {
		AUTO, IDLE, HOME, MANUAL
	}
	

	public enum HopperState {
		RUNNING, STOPPED
	}
	
	public enum TurretAutoState {
		AIMING, AIMED
	}
	
	private enum HomingState {
		LEFT, RIGHT
	}
	
	private static final Shooter shooterInstance = new Shooter();
	
	private ShooterState currentState;
	private TurretState turretState;
	private HopperState hopperState;
	private TurretAutoState turretAutoState;
	private HomingState homingState;

	private double discardFrames;
	private Rotation desiredAngle;
	private boolean homed;
	
	private OrangeDrive orangeDrive;
	private Turret turret;
	private Flywheel flywheel;
	private Servo hood;
	private DigitalInput homeSensor;
	private Hopper hopper;
	
	private double desiredSpeed;
	private double startHome;
	
	private Interpolable lookupTable;
	
	public static Shooter getInstance(){
		return shooterInstance;
	}
	
	public void resetState()
	{
		currentState = ShooterState.IDLE;
		turretState = TurretState.IDLE;
	}
	
	private Shooter(){
		RUNNINGSPEED = 10;
		hood = new Servo(Constants.ServoId);
		hood.setBounds(1,0,0,0,2);
		currentState = ShooterState.IDLE;
		turretState = TurretState.IDLE;
		hopperState = HopperState.STOPPED;
		desiredAngle = new Rotation();
		turret = new Turret(Constants.RightTurretId);
		flywheel = new Flywheel(Constants.MasterFlywheelId, Constants.SlaveFlywheelId);
		homeSensor = new DigitalInput(1);
		orangeDrive = OrangeDrive.getInstance();
		hopper = Hopper.getInstance();
		desiredSpeed = Constants.InitialFlywheelSpeed;
		homed = false;
		
		lookupTable.addNumber(10.0, 10.0);
	}
	
	@Override
	public synchronized void update() {
		NetworkTable.getTable("/shooter").putNumber("rpms", flywheel.getSpeed());
		NetworkTable.getTable("/shooter").putNumber("setpoint", desiredSpeed);
		switch(hopperState){
			case RUNNING:
				hopper.setRun(true);
				break;
			case STOPPED:
				hopper.setRun(false);
				break;
		}
		switch(turretState){
			case AUTO:
				switch(turretAutoState){
					case AIMING:				
						turret.setAngle(desiredAngle.rotateBy(Rotation.fromDegrees(Constants.TurretCameraOffset)));
						if(turret.isDone()){
							discardFrames = 0;
							turretAutoState = TurretAutoState.AIMED;
						}
						break;
					case AIMED:
						updateDesiredAngle();
						if(Math.abs(turret.getAngle().rotateBy(desiredAngle.inverse()).rotateBy(Rotation.fromDegrees(-Constants.TurretCameraOffset)).getDegrees()) > .5) {
							turretAutoState = TurretAutoState.AIMING;
						}
						break;
				}				
				break;					
			case MANUAL:
				break;
			case HOME:
				if(homingState == HomingState.RIGHT){
					turret.setManual(0.2);
				} else {
					turret.setManual(-0.2);
				}
				if(!homeSensor.get()){
					turret.setManual(0);
					turret.resetPosition();
					turretState = TurretState.IDLE;
					homed = true;
					break;
				}

				if(homingState == HomingState.LEFT){	
					if(System.currentTimeMillis() - startHome > 2000){
						turretState = TurretState.IDLE;
					}
				} else {
					if(System.currentTimeMillis() - startHome > 1000){
						startHome = System.currentTimeMillis();
						homingState = HomingState.LEFT;
						}
				}
				break;
			case IDLE:
				turret.setAngle(Rotation.fromDegrees(0));
				break;
		}
		
		switch(currentState){
			case READY:
				if(turretState != TurretState.AUTO){
					discardFrames = 15;
					updateDesiredAngle();
					turretState = TurretState.AUTO;
					turretAutoState = TurretAutoState.AIMING;
				} else {
					flywheel.setSetpoint(desiredSpeed);
				}
			break;
			case SHOOT:
				if(turretState != TurretState.AUTO){
					discardFrames = 15;
					updateDesiredAngle();
					turretState = TurretState.AUTO;
					turretAutoState = TurretAutoState.AIMING;
				} else {
					flywheel.setSetpoint(desiredSpeed);
					if(turretAutoState == TurretAutoState.AIMED){
						if (flywheel.isDone()) {
							hopperState = HopperState.RUNNING;							
						}
					}
				}
				break;
			case IDLE:
				if(turretState != TurretState.HOME || turretState != TurretState.MANUAL){
					turretState = TurretState.IDLE;
					//turret.setAngle(Rotation.fromDegrees(45).rotateBy(orangeDrive.getGyroAngle().inverse()));
				}
				flywheel.setVoltage(0.75);
				hood.set(1);
				hopperState = HopperState.STOPPED;
				break;
		}
	}
	
	public synchronized void setState(ShooterState wantedState){
		currentState = wantedState;
	}
	
	public synchronized void setHome(){
		currentState = ShooterState.IDLE;
		startHome = System.currentTimeMillis();
		homingState = HomingState.RIGHT;
		turretState = TurretState.HOME;
	}
	
	public synchronized void setSpeed(double speed){
		this.desiredSpeed = speed;
	}
	
	public synchronized void updateDesiredAngle(){
		if(currentState == ShooterState.SHOOT && turretAutoState == TurretAutoState.AIMED){
			//System.out.println("");
		} else {
			discardFrames++;
			if(discardFrames > 15){
				desiredAngle = turret.getAngle().rotateBy(Rotation.fromDegrees(Dashcomm.get("boilerXAngle", 0)));
				desiredSpeed = lookupTable.interpolate(Dashcomm.get("boilerYAngle", 0));		
			}
		}
	}
	
	public double getSpeed(){
		return flywheel.getSpeed();
	}
	
	public double getPower(){
		return flywheel.getOutputVoltage();
	}
	
	public synchronized void setTurretAngle(Rotation setAngle){
		turretState = TurretState.MANUAL;
		turret.setAngle(setAngle);
	}
	
	public boolean isDone(){
		/*
		switch(currentState){
			case READY:
				return turretAutoState == TurretAutoState.AIMED;		
			case SHOOT:
				return hopperState == HopperState.RUNNING;
			case IDLE:
				return turretState != TurretState.HOME;
		}
		*/
		return true;
	}
	
	public boolean isHomed(){
		return homed;
	}
	
	public void setHopper(HopperState state){
		hopperState = state;
	}
}
