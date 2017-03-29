package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.utility.Constants;
import org.usfirst.frc.team3476.utility.Dashcomm;
import org.usfirst.frc.team3476.utility.Rotation;
import org.usfirst.frc.team3476.utility.Threaded;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Servo;

public class Shooter extends Threaded {
	public enum ShooterState {
		READY, SHOOT, IDLE
	}
	
	public enum TurretState {
		AIMING, AIMED, IDLE, HOME, MANUAL
	}
	
	private enum HomingState {
		LEFT, RIGHT
	}
	
	private static final Shooter shooterInstance = new Shooter();
	
	private ShooterState currentState;
	private TurretState turretState;
	private HomingState homingState;
	CANTalon hopper = new CANTalon(6);
	CANTalon spinningHopper = new CANTalon(7);
	CANTalon redwheel = new CANTalon(11);
	
	private double discardFrames;
	private Rotation desiredAngle;
	
	private Turret turret;
	private Flywheel flywheel;
	private Servo hood;
	private DigitalInput homeSensor;
	
	private double speed;
	private double startHome;
	
	public static Shooter getInstance(){
		return shooterInstance;
	}
	
	private Shooter(){
		RUNNINGSPEED = 10;
		hood = new Servo(0);
		hood.setBounds(1,0,0,0,2);
		currentState = ShooterState.IDLE;
		turretState = TurretState.IDLE;
		desiredAngle = new Rotation();
		turret = new Turret(Constants.RightTurretId);
		flywheel = new Flywheel(Constants.LeftMasterFlywheelId, Constants.LeftSlaveFlywheelId);
		homeSensor = new DigitalInput(1);
	}
	int cnt = 0;
	@Override
	public synchronized void update() {
		cnt++;
		if (cnt % 5 == 0)
		{
			System.out.println("TurretState: " + turretState);
			System.out.println("ShootingState: " + currentState);
		}
		switch(turretState){
			case AIMING:
				turret.setAngle(desiredAngle.rotateBy(Rotation.fromDegrees(1)));
				if(turret.isDone()){
					discardFrames = 0;
					turretState = TurretState.AIMED;
				}
				break;
			case AIMED:
				updateDesiredAngle();
				if(Math.abs(turret.getAngle().rotateBy(desiredAngle.inverse()).rotateBy(Rotation.fromDegrees(1)).getDegrees()) > 0.5) {
					turretState = TurretState.AIMING;
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
					break;
				}
				if(System.currentTimeMillis() - startHome > 1000){
					startHome = System.currentTimeMillis();
					if(homingState == HomingState.LEFT){	
						System.out.println("failed to home");
					} else {
						homingState = HomingState.LEFT;
					}
				}
				break;
			case IDLE:
				turret.setManual(0);
				break;
		}
		
		switch(currentState){
			case READY:
				if(turretState != TurretState.AIMING && turretState != TurretState.AIMED){
					discardFrames = 15;
					updateDesiredAngle();
					turretState = TurretState.AIMING;
				} else {
					flywheel.setSetpoint(speed);
				}
			break;
			case SHOOT:
				if(turretState == TurretState.IDLE){
					discardFrames = 15;
					turretState = TurretState.AIMING;
					updateDesiredAngle();
				} else {
					flywheel.setSetpoint(speed);
					if(turretState == TurretState.AIMED){
						hopper.set(-1);
						spinningHopper.set(-0.55);
						redwheel.set(-0.8);
					}
				}
				/*
				if(turretState == TurretState.AIMED && flywheel.isDone()){
					// run hopper
				}
				*/
				break;
			case IDLE:
				if(turretState != TurretState.HOME){
					turretState = TurretState.IDLE;
				}
				flywheel.setPercent(0);
				hood.set(1);
				spinningHopper.set(0);
				hopper.set(0);
				redwheel.set(0);
				break;
		}
	}
	
	public synchronized void setState(ShooterState wantedState){
		currentState = wantedState;
	}
	
	public synchronized void setHome(){
		startHome = System.currentTimeMillis();
		homingState = HomingState.RIGHT;
		turretState = TurretState.HOME;
	}
	
	public synchronized void setSpeed(double speed){
		this.speed = speed;
	}
	
	public synchronized void updateDesiredAngle(){
		if(currentState == ShooterState.SHOOT && turretState == TurretState.AIMED){
			//System.out.println("");
		} else {
			double angleOff = Dashcomm.get("boilerAngle", 0);
			discardFrames++;
			if(discardFrames > 15){
				desiredAngle = turret.getAngle().rotateBy(Rotation.fromDegrees(angleOff));
			}
		}
	}
	
	public double getSpeed(){
		return flywheel.getSpeed();
	}
	
	public double getPower(){
		return flywheel.getOutputVoltage();
	}
}
