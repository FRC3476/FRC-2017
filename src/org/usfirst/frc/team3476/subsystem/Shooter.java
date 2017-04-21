package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.subsystem.Hopper.HopperState;
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
		AUTO, IDLE, HOME
	}
	
	public enum TurretAutoState {
		AIMING, AIMED, DONE
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
	
	private double speedOffset = 120;
	private double desiredSpeed;
	private double startHome;
	private double turretStartTime;
	
	private Interpolable lookupTable1;
	private Interpolable lookupTable07;
	private Interpolable lookupTable09;
	
	public boolean isFlywheelDone()
	{
		return flywheel.isDone();
	}
	
	public void enableFlywheel()
	{
		flywheel.enable();
	}
	
	public void setFlywheelSpeed(double setpoint)
	{
		flywheel.setSetpoint(setpoint);
	}
	
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
		lookupTable1 = new Interpolable();
		lookupTable09 = new Interpolable();
		lookupTable07 = new Interpolable();

		/*
		
		lookupTable07.addNumber(87.744, 3170.0 + speedOffset);
		lookupTable07.addNumber(89.833, 3250.0 + speedOffset);
		lookupTable07.addNumber(92.04, 3300.0 + speedOffset);
		lookupTable07.addNumber(96.74, 3350.0 + speedOffset);
		lookupTable07.addNumber(99.289, 3450.0 + speedOffset);
		*/
		
		/*
		lookupTable08.addNumber(87.744, 3170.0);
		lookupTable08.addNumber(89.833, 3250.0);
		lookupTable08.addNumber(92.04, 3280.0);
		lookupTable08.addNumber(96.74, 3330.0);
		lookupTable08.addNumber(99.289, 3450.0);
		lookupTable08.addNumber(101.973, 3470.0);
		lookupTable08.addNumber(107.800, 3570.0);
		lookupTable08.addNumber(110.971, 3620.0);
		lookupTable08.addNumber(114.333, 3715.0);
		lookupTable08.addNumber(117.906, 3750.0);
		lookupTable08.addNumber(121.740, 3870.0);
		lookupTable08.addNumber(125.767, 3980.0);
		lookupTable08.addNumber(130.103, 4010.0);
		lookupTable08.addNumber(134.750, 4050.0);
		lookupTable08.addNumber(139.741, 4080.0);
		lookupTable08.addNumber(145.115, 4210.0);
		lookupTable08.addNumber(150.920, 4320.0);
		lookupTable08.addNumber(157.208, 4450.0);
		*/
		
		/*
		lookupTable1.addNumber(99.289, 3380.0 + speedOffset);
		lookupTable1.addNumber(101.973, 3450.0 + speedOffset);
		lookupTable1.addNumber(107.800, 3560.0 + speedOffset);
		lookupTable1.addNumber(110.971, 3610.0 + speedOffset);
		lookupTable1.addNumber(114.333, 3675.0 + speedOffset);
		lookupTable1.addNumber(117.906, 3730.0 + speedOffset);
		lookupTable1.addNumber(121.710, 3800.0 + speedOffset);
		lookupTable1.addNumber(125.767, 3900.0 + speedOffset);
		lookupTable1.addNumber(130.103, 3930.0 + speedOffset);
		lookupTable1.addNumber(134.750, 3980.0 + speedOffset);
		lookupTable1.addNumber(139.741, 4090.0 + speedOffset);
		lookupTable1.addNumber(145.115, 4140.0 + speedOffset);
		lookupTable1.addNumber(150.920, 4190.0 + speedOffset);
		lookupTable1.addNumber(157.208, 4320.0 + speedOffset);
		lookupTable1.addNumber(188.650, 4780.0 + speedOffset);
		*/
		
		lookupTable09.addNumber(99.0, 3600.0);
		lookupTable09.addNumber(118.0, 3780.0);
		lookupTable09.addNumber(135.0, 4050.0);
		lookupTable09.addNumber(145.0, 4200.0);

		lookupTable07.addNumber(102.0, 3500.0);
		lookupTable07.addNumber(92.0, 3350.0);



		
		
		
		hood.set(0.7);
	}
	
	@Override
	public synchronized void update() {
		NetworkTable.getTable("/shooter").putNumber("rpms", flywheel.getSpeed());
		NetworkTable.getTable("/shooter").putNumber("setpoint", desiredSpeed);
		NetworkTable.getTable("/shooter").putNumber("angle", turret.getAngle().getDegrees());
		NetworkTable.getTable("/shooter").putNumber("current", hopper.getCurrent());
		hopper.setState(hopperState);
		if(Constants.TurretEnabled){
			switch(turretState){
				case AUTO:
					switch(turretAutoState){
						case AIMING:			
							turret.setAngle(desiredAngle.rotateBy(Rotation.fromDegrees(Constants.TurretCameraOffset)));
							if(turret.isDone()){
								turretAutoState = TurretAutoState.AIMED;	
								turretStartTime = System.currentTimeMillis();
							}
							break;
						case AIMED:
							/*
							if(Math.abs(turret.getAngle().rotateBy(desiredAngle.inverse()).rotateBy(Rotation.fromDegrees(-Constants.TurretCameraOffset)).getDegrees()) > .5) {
								turretAutoState = TurretAutoState.AIMING;
								updateDesiredAngle();
							}
							*/
							if(System.currentTimeMillis() - turretStartTime > 900){
								updateDesiredAngle();
								turret.setAngle(desiredAngle.rotateBy(Rotation.fromDegrees(Constants.TurretCameraOffset)));
								if(!turret.isDone()) {
									turretAutoState = TurretAutoState.AIMING;
								} else {
									turretAutoState = TurretAutoState.DONE;
									updateDesiredSpeed();
								}
									
							}
							break;
						case DONE:
							break;
					}				
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
						if(System.currentTimeMillis() - startHome > 1500){
							turretState = TurretState.IDLE;
						}
					} else {
						if(System.currentTimeMillis() - startHome > 1500){
							startHome = System.currentTimeMillis();
							homingState = HomingState.LEFT;
							}
					}
					break;
				case IDLE:
					break;
			}
		}
		switch(currentState){
			case READY:
				if(turretState != TurretState.AUTO){
					turretState = TurretState.AUTO;
					turretAutoState = TurretAutoState.AIMING;
					updateDesiredAngle();
				} else {
					flywheel.setSetpoint(desiredSpeed);
				}
			break;
			case SHOOT:
				if(turretState != TurretState.AUTO){
					turretState = TurretState.AUTO;
					turretAutoState = TurretAutoState.AIMING;
					updateDesiredAngle();
				} else {
					if(turretAutoState == TurretAutoState.DONE){
						flywheel.setSetpoint(desiredSpeed);
						if (flywheel.isDone()) {
							hopperState = HopperState.RUNNING;							
						}
					}
				}
				break;
			case IDLE:
				if(turretState != TurretState.HOME){
					turretAutoState = TurretAutoState.AIMING;
					turretState = TurretState.IDLE;
				}
				flywheel.setVoltage(0);
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
		} else {
			if(Dashcomm.get("isBoilerVisible", false)){
				desiredAngle = turret.getAngle().rotateBy(Rotation.fromDegrees(Dashcomm.get("boilerXAngle", 0)));					
			} else {
				desiredAngle = turret.getAngle().rotateBy(Rotation.fromDegrees(0));
				turretState = TurretState.IDLE;
			}		
		}
	}
	
	public synchronized void updateDesiredSpeed(){
		/*
		double distance = Dashcomm.get("boilerYAngle", 0);
		if(distance < 100){
			desiredSpeed = lookupTable07.interpolate(distance);
			hood.set(0.7);
		} else {
			desiredSpeed = lookupTable1.interpolate(distance);
			hood.set(0.9);
		}
		*/
	}
	
	public double getSpeed(){
		return flywheel.getSpeed();
	}
	
	public double getPower(){
		return flywheel.getOutputVoltage();
	}
	
	public synchronized void setTurretAngle(Rotation setAngle){
		turretState = TurretState.IDLE;
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

	public double getHopperCurrent()
	{
		return hopper.getCurrent();
	}
}
