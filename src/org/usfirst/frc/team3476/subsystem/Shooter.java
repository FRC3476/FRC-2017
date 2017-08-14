package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.utility.CircularQueue;
import org.usfirst.frc.team3476.utility.Constants;
import org.usfirst.frc.team3476.utility.Flywheel;
import org.usfirst.frc.team3476.utility.InterpolableValue;
import org.usfirst.frc.team3476.utility.InterpolatingDouble;
import org.usfirst.frc.team3476.utility.Rotation;
import org.usfirst.frc.team3476.utility.Threaded;
import org.usfirst.frc.team3476.utility.Turret;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Servo;

public class Shooter extends Threaded {
	private enum HomingState {
		LEFT, RIGHT
	}

	public enum ShooterState {
		SHOOT, IDLE
	}

	public enum TurretAutoState {
		AIMING, AIMED
	}

	public enum TurretState {
		AUTO, IDLE, HOME
	}

	private static final Shooter shooterInstance = new Shooter();

	public static Shooter getInstance() {
		return Shooter.shooterInstance;
	}

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
	private CircularQueue<InterpolatingDouble> lookupTable1;
	private CircularQueue<InterpolatingDouble> lookupTable09;
	private CircularQueue<InterpolatingDouble> lookupTable08;

	private CircularQueue<InterpolatingDouble> lookupTable07;

	private Shooter() {
		hood = new Servo(Constants.ServoId);
		hood.setBounds(1, 0, 0, 0, 2);
		currentState = ShooterState.IDLE;
		turretState = TurretState.IDLE;
		turret = new Turret(Constants.RightTurretId);
		flywheel = new Flywheel(Constants.MasterFlywheelId, Constants.SlaveFlywheelId);
		homeSensor = new DigitalInput(1);
		hopper = Hopper.getInstance();
		homed = false;

		lookupTable1 = new CircularQueue<>(20);
		lookupTable09 = new CircularQueue<>(20);
		lookupTable08 = new CircularQueue<>(20);
		lookupTable07 = new CircularQueue<>(20);

		/*
		 * lookupTable07.addNumber(87.744, 3170.0 + speedOffset);
		 * lookupTable07.addNumber(89.833, 3250.0 + speedOffset);
		 * lookupTable07.addNumber(92.04, 3300.0 + speedOffset);
		 * lookupTable07.addNumber(96.74, 3350.0 + speedOffset);
		 * lookupTable07.addNumber(99.289, 3450.0 + speedOffset);
		 */

		/*
		 * lookupTable08.addNumber(87.744, 3170.0);
		 * lookupTable08.addNumber(89.833, 3250.0);
		 * lookupTable08.addNumber(92.04, 3280.0);
		 * lookupTable08.addNumber(96.74, 3330.0);
		 * lookupTable08.addNumber(99.289, 3450.0);
		 * lookupTable08.addNumber(101.973, 3470.0);
		 * lookupTable08.addNumber(107.800, 3570.0);
		 * lookupTable08.addNumber(110.971, 3620.0);
		 * lookupTable08.addNumber(114.333, 3715.0);
		 * lookupTable08.addNumber(117.906, 3750.0);
		 * lookupTable08.addNumber(121.740, 3870.0);
		 * lookupTable08.addNumber(125.767, 3980.0);
		 * lookupTable08.addNumber(130.103, 4010.0);
		 * lookupTable08.addNumber(134.750, 4050.0);
		 * lookupTable08.addNumber(139.741, 4080.0);
		 * lookupTable08.addNumber(145.115, 4210.0);
		 * lookupTable08.addNumber(150.920, 4320.0);
		 * lookupTable08.addNumber(157.208, 4450.0);
		 */

		/*
		 * lookupTable1.addNumber(99.289, 3380.0 + speedOffset);
		 * lookupTable1.addNumber(101.973, 3450.0 + speedOffset);
		 * lookupTable1.addNumber(107.800, 3560.0 + speedOffset);
		 * lookupTable1.addNumber(110.971, 3610.0 + speedOffset);
		 * lookupTable1.addNumber(114.333, 3675.0 + speedOffset);
		 * lookupTable1.addNumber(117.906, 3730.0 + speedOffset);
		 * lookupTable1.addNumber(121.710, 3800.0 + speedOffset);
		 * lookupTable1.addNumber(125.767, 3900.0 + speedOffset);
		 * lookupTable1.addNumber(130.103, 3930.0 + speedOffset);
		 * lookupTable1.addNumber(134.750, 3980.0 + speedOffset);
		 * lookupTable1.addNumber(139.741, 4090.0 + speedOffset);
		 * lookupTable1.addNumber(145.115, 4140.0 + speedOffset);
		 * lookupTable1.addNumber(150.920, 4190.0 + speedOffset);
		 * lookupTable1.addNumber(157.208, 4320.0 + speedOffset);
		 * lookupTable1.addNumber(188.650, 4780.0 + speedOffset);
		 */

		lookupTable1.add(new InterpolableValue<>(1000.0, new InterpolatingDouble(1000.0)));

		lookupTable09.add(new InterpolableValue<>(99.0, new InterpolatingDouble(3510.0)));
		lookupTable09.add(new InterpolableValue<>(118.0, new InterpolatingDouble(3780.0)));
		lookupTable09.add(new InterpolableValue<>(126.0, new InterpolatingDouble(3950.0)));
		lookupTable09.add(new InterpolableValue<>(135.0, new InterpolatingDouble(4050.0))); 
		lookupTable09.add(new InterpolableValue<>(145.0, new InterpolatingDouble(4200.0)));

		lookupTable08.add(new InterpolableValue<>(1000.0, new InterpolatingDouble(10000.0)));

		lookupTable07.add(new InterpolableValue<>(92.0, new InterpolatingDouble(3340.0)));
		lookupTable07.add(new InterpolableValue<>(102.0, new InterpolatingDouble(3500.0))); 
		hood.set(0.4);
	}

	public Rotation getAngle() {
		return turret.getAngle();
	}

	public Rotation getDesiredAngle() {
		long time = VisionServer.getInstance().getBoilerData().getTime();
		double angle = VisionServer.getInstance().getBoilerData().getAngle();
		Rotation gyroComp = RobotTracker.getInstance().getGyroAngle(time).inverse().rotateBy(OrangeDrive.getInstance().getGyroAngle());
		Rotation turretComp = RobotTracker.getInstance().getTurretAngle(time);
		return turretComp.rotateBy(Rotation.fromDegrees(angle));
	}

	public void getDesiredSpeed() {
		double distance = 0;// VisionTracking.getInstance().getBoilerDistance();
		if (distance < 100) {

			hood.set(0.7);
		} else {
			hood.set(0.9);
		}
	}

	public boolean isDone() {
		return true;
	}

	public boolean isHomed() {
		return homed;
	}

	public void resetState() {
		currentState = ShooterState.IDLE;
		turretState = TurretState.IDLE;
	}

	public synchronized void setHome() {
		startHome = System.currentTimeMillis();
		homingState = HomingState.RIGHT;
		turretState = TurretState.HOME;
	}

	public synchronized void setState(ShooterState wantedState) {
		switch (wantedState) {
		case SHOOT:
			if (currentState != ShooterState.SHOOT) {
				turretState = TurretState.AUTO;
				turretAutoState = TurretAutoState.AIMING;
				turret.setAngle(getDesiredAngle());
			}
			break;
		case IDLE:
			if(turretState != TurretState.HOME){
				turretState = TurretState.IDLE;				
			}
			break;
		}
		currentState = wantedState;
	}

	public synchronized void setTurretAngle(Rotation setAngle) {
		turretState = TurretState.IDLE;
		turret.setAngle(setAngle);
	}

	@Override
	public synchronized void update() {

		switch (turretState) {
		case AUTO:
			break;
		case IDLE:
			break;
		case HOME:
			if (homingState == HomingState.RIGHT) {
				turret.setManual(0.2);
			} else {
				turret.setManual(-0.2);
			}
			if (!homeSensor.get()) {
				turret.setManual(0);
				turret.resetPosition(30);
				turretState = TurretState.IDLE;
				homed = true;
				break;
			}
			if (homingState == HomingState.LEFT) {
				if (System.currentTimeMillis() - startHome > 1500) {
					turret.setManual(0);
					turretState = TurretState.IDLE;
					DriverStation.reportError("Failed to home turret", false);
				}
			} else {
				if (System.currentTimeMillis() - startHome > 1500) {
					startHome = System.currentTimeMillis();
					homingState = HomingState.LEFT;
				}
			}
			break;
		}
		switch (currentState) {
		case SHOOT:
			break;
		case IDLE:
			break;
		}
	}
}
