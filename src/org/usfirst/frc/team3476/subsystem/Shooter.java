package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.utility.Constants;
import org.usfirst.frc.team3476.utility.Dashcomm;
import org.usfirst.frc.team3476.utility.Rotation;
import org.usfirst.frc.team3476.utility.Threaded;

import edu.wpi.first.wpilibj.Servo;

public class Shooter extends Threaded {
	public enum ShooterState {
		HOMING, SHOOTING, IDLE
	}
	
	public enum TurretState {
		AIMING, AIMED, IDLE
	}
	
	private static final Shooter shooterInstance = new Shooter();
	
	private ShooterState currentState;
	private TurretState turretState;
	
	private double discardFrames;
	private Rotation desiredAngle;
	
	private Turret turret;
	private Flywheel flywheel;
	public Servo hood;
	
	private double speed;
	
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
	}
	
	@Override
	public synchronized void update() {
		switch(turretState){
			case AIMING:
				turret.setAngle(desiredAngle);
				if(turret.isDone()){
					discardFrames = 0;
					turretState = TurretState.AIMED;
				}
				break;
			case AIMED:
					//updateDesiredAngle();
					if(Math.abs(turret.getAngle().rotateBy(desiredAngle.inverse()).getDegrees()) > 0.5) {
						turretState = TurretState.AIMING;
					}
				break;
			case IDLE:
				turret.setManual(0);
				break;
		}
		
		switch(currentState){
			case SHOOTING:
				if(turretState == TurretState.IDLE){
					discardFrames = 25;
					updateDesiredAngle();
					turretState = TurretState.AIMING;
				} else {

					flywheel.setSetpoint(speed);
				}
				break;
			case HOMING:
				break;
			case IDLE:
				turretState = TurretState.IDLE;
				flywheel.setPercent(0);
				hood.set(1);
				break;
		}
	}
	
	public synchronized void setState(ShooterState wantedState){
		currentState = wantedState;
	}
	
	public synchronized void setSpeed(double speed){
		this.speed = speed;
	}
	
	public synchronized void updateDesiredAngle(){
		double angleOff = Dashcomm.get("boilerAngle", 0);
		discardFrames++;
		if(discardFrames > 25){
			desiredAngle = desiredAngle.rotateBy(Rotation.fromDegrees(angleOff));
		}
	}
	
	public double getSpeed(){
		return flywheel.getSpeed();
	}
	
	public double getPower(){
		return flywheel.getOutputVoltage();
	}
}
