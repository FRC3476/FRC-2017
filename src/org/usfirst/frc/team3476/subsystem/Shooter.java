package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.subsystem.Hopper.HopperState;
import org.usfirst.frc.team3476.utility.Constants;
import org.usfirst.frc.team3476.utility.Dashcomm;
import org.usfirst.frc.team3476.utility.Flywheel;
import org.usfirst.frc.team3476.utility.Interpolable;
import org.usfirst.frc.team3476.utility.Rotation;
import org.usfirst.frc.team3476.utility.Threaded;
import org.usfirst.frc.team3476.utility.Turret;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Servo;

public class Shooter extends Threaded {
	public enum ShooterState {
		SHOOT, IDLE
	}
	
	public enum TurretState {
		AUTO, IDLE, HOME
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
	private TurretAutoState turretAutoState;
	private HomingState homingState;

	private boolean homed;
	
	private Turret turret;
	private Flywheel flywheel;
	private Servo hood;
	private DigitalInput homeSensor;
	private Hopper hopper;
	
	private double startHome;
	private double turretStartTime;
	
	private Interpolable lookupTable1;
	private Interpolable lookupTable09;
	private Interpolable lookupTable08;
	private Interpolable lookupTable07;
	
	public static Shooter getInstance(){
		return shooterInstance;
	}
	
	public void resetState()
	{
		currentState = ShooterState.IDLE;
		turretState = TurretState.IDLE;
	}
	
	private Shooter(){
		hood = new Servo(Constants.ServoId);
		hood.setBounds(1,0,0,0,2);
		currentState = ShooterState.IDLE;
		turretState = TurretState.IDLE;
		turret = new Turret(Constants.RightTurretId);
		flywheel = new Flywheel(Constants.MasterFlywheelId, Constants.SlaveFlywheelId);
		homeSensor = new DigitalInput(1);
		hopper = Hopper.getInstance();
		homed = false;
		lookupTable1 = new Interpolable();
		lookupTable09 = new Interpolable();
		lookupTable08 = new Interpolable();
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
		
		lookupTable1.addNumber(1000.0, 10000.0);
		
		lookupTable09.addNumber(99.0, 3510.0);
		lookupTable09.addNumber(118.0, 3780.0); //should be 0.8 around here
		lookupTable09.addNumber(126.0, 3950.0); //good around here
		lookupTable09.addNumber(135.0, 4050.0); //good around here		
		lookupTable09.addNumber(145.0, 4200.0);
		
		lookupTable08.addNumber(1000.0, 10000.0);
		
		lookupTable07.addNumber(92.0, 3340.0);		
		lookupTable07.addNumber(102.0, 3500.0); // these values are guddi
		hood.set(0.4);
	}
	
	@Override
	public synchronized void update() {
		switch(currentState){
		case SHOOT:
			switch(turretState){
			case AUTO:
				if(turret.isDone()){
					turret.setAngle(getDesiredAngle());
					turretStartTime = System.currentTimeMillis();
				}
				break;
			case IDLE:
				break;
			case HOME:
				break;
			}
		case IDLE:
			
		}
	}
	
	public synchronized void setState(ShooterState wantedState){
		switch (wantedState){
		case SHOOT:
			if(currentState != ShooterState.SHOOT){
				turretState = TurretState.AUTO;
				turretAutoState = TurretAutoState.AIMING;
			}
			break;
		case IDLE:
			turretState = TurretState.IDLE;
			break;
		}
		currentState = wantedState;
	}
	
	public synchronized void setHome(){
		currentState = ShooterState.IDLE;
		startHome = System.currentTimeMillis();
		homingState = HomingState.RIGHT;
		turretState = TurretState.HOME;
	}
	
	public Rotation getDesiredAngle(){
		long time = VisionServer.getInstance().getBoilerData().time;
		double angle = VisionServer.getInstance().getBoilerData().angle;
		Rotation gyroComp = RobotTracker.getInstance().getGyroAngle(time).inverse().rotateBy(OrangeDrive.getInstance().getGyroAngle());
		return RobotTracker.getInstance().getTurretAngle(time).rotateBy(gyroComp).rotateBy(Rotation.fromDegrees(angle));
	}
	
	public synchronized void updateDesiredSpeed(){		
		double distance = 0;//VisionTracking.getInstance().getBoilerDistance();
		if(distance < 100){
			
			hood.set(0.7);
		} else {
			
			hood.set(0.9);
		}		
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
	
	public Rotation getAngle(){
		return turret.getAngle();
	}
}
