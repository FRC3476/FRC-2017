package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.utility.Constants;
import org.usfirst.frc.team3476.utility.Dashcomm;
import org.usfirst.frc.team3476.utility.OrangeUtility;
import org.usfirst.frc.team3476.utility.Path;
import org.usfirst.frc.team3476.utility.PurePursuitController;
import org.usfirst.frc.team3476.utility.Rotation;
import org.usfirst.frc.team3476.utility.SynchronousPid;
import org.usfirst.frc.team3476.utility.Threaded;
import org.usfirst.frc.team3476.utility.Translation;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.MotionProfileStatus;
import com.ctre.CANTalon.StatusFrameRate;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

/* Inspiration from Team 254 */

public class OrangeDrive extends Threaded {
	public enum DriveState {
		MANUAL, AUTO, GEAR
	}
	
	public enum AutoState {
		DRIVING, ROTATING, DONE
	}
	
	private enum GearState {
		TURNING, DRIVING, REVERSING, DONE
	}
	
	public enum ShiftState {
		MANUAL, AUTO
	}

	private double quickStopAccumulator;
	private double lastTime;
	private double lastValue;
	private double gearReversingTime;
	private double gearInitialDistance;
	
	private ADXRS450_Gyro gyroSensor = new ADXRS450_Gyro(SPI.Port.kOnboardCS0);
	private SynchronousPid turningDriver = new SynchronousPid(Constants.TurningP, 0, Constants.TurningD, 0);
	private Rotation desiredAngle;
	private double desiredDistance;
	
	private Rotation gyroOffset;
	
	private ShiftState shiftState = ShiftState.AUTO;
	private DriveState driveState = DriveState.MANUAL;
	private AutoState autoState;
	private GearState gearState;

	private Gear gear = Gear.getInstance();
	private static final OrangeDrive driveInstance = new OrangeDrive();

	private CANTalon leftTalon, rightTalon, leftSlaveTalon, rightSlaveTalon;
	//private RobotTracker robotState;
	private PurePursuitController autonomousDriver;
	private DriveVelocity autoDriveVelocity;
	private Solenoid driveShifters = new Solenoid(Constants.ShifterSolenoidId);	
	private int driveMultiplier = 1;
	
	public static OrangeDrive getInstance() {
		return driveInstance;
	}
	
	public void updatePIDF(double P, double I, double D, double F)
	{
		turningDriver.setPIDF(P, I, D, F);
	}
	
	private OrangeDrive() {
		RUNNINGSPEED = 10;
		
		leftTalon = new CANTalon(Constants.LeftMasterDriveId);
		rightTalon = new CANTalon(Constants.RightMasterDriveId);

		leftTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		rightTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		
		// Quadrature updates at 100ms

		leftTalon.setStatusFrameRateMs(StatusFrameRate.QuadEncoder, 10);
		rightTalon.setStatusFrameRateMs(StatusFrameRate.QuadEncoder, 10);

		leftTalon.configEncoderCodesPerRev(1024);
		rightTalon.configEncoderCodesPerRev(1024);

		leftTalon.reverseOutput(false);
		leftTalon.reverseSensor(true);
		rightTalon.reverseOutput(true);
		rightTalon.reverseSensor(false);

		leftSlaveTalon = new CANTalon(Constants.LeftSlaveDriveId);
		rightSlaveTalon = new CANTalon(Constants.RightSlaveDriveId);
		
		leftSlaveTalon.changeControlMode(TalonControlMode.Follower);
		leftSlaveTalon.set(leftTalon.getDeviceID());
		rightSlaveTalon.changeControlMode(TalonControlMode.Follower);
		rightSlaveTalon.set(rightTalon.getDeviceID());
		
		leftTalon.changeControlMode(TalonControlMode.Speed);
		rightTalon.changeControlMode(TalonControlMode.Speed);
		turningDriver.setOutputRange(216, -216);
		turningDriver.setSetpoint(0);
		gyroOffset = new Rotation();
	}

	@Override
	public synchronized void update() {
		switch (driveState) {
		case MANUAL:
			break;
		case AUTO:
			updateAutoPath();
			break;
		case GEAR:
			updateGearPath();
			break;
		}
	}
	
	
	public double scaleJoystickValues(double rawValue){
		if (Math.abs(rawValue) >= Constants.MinimumControllerInput) {
			double scaledValue = (rawValue * (Math.abs(rawValue) - Constants.MinimumControllerInput)) / ((0.85) * Math.abs(rawValue));
			return scaledValue;
			// 0.7 = Constants.MaximumControllerInput - Constants.MinimumControllerInput
		} else {
			return 0;
		}
	}

	public synchronized void setAutoPath(Path autoPath, boolean isReversed) {
		//robotState = RobotTracker.getInstance();
		// PurePursuitController(double lookAheadDistance, double robotSpeed,
		// double robotDiameter, Path robotPath)
		if(driveState != DriveState.AUTO){
			driveState = DriveState.AUTO;
			setBrake(true);
			shiftDown();
		}

		if(autoState != AutoState.DRIVING){
			autoState = AutoState.DRIVING;					
		}
		autonomousDriver = new PurePursuitController(autoPath, isReversed);
		updateAutoPath();
		//System.out.println("drive");
	}
	
	public synchronized void setRotation(Rotation desiredRotation){
		if(driveState != DriveState.AUTO){
			driveState = DriveState.AUTO;
			setBrake(true);
			shiftUp();
		}

		if(autoState != AutoState.ROTATING){
			autoState = AutoState.ROTATING;
		}
		desiredAngle = desiredRotation;
	}
	
	public synchronized void setGearPath() {
		if(driveState != DriveState.GEAR){
			driveState = DriveState.GEAR;
			gearState = GearState.DRIVING;
			setBrake(true);
			shiftDown();
			updateDesiredAngle();
			gearInitialDistance = getDistance();
			updateGearPath();			
		}
	}
	
	public synchronized void setManualGearPath(){
		if(driveState != DriveState.GEAR){
			driveState = DriveState.GEAR;
			gearState = GearState.REVERSING;
			setBrake(true);
			shiftDown();
			updateDesiredAngle();
			gearReversingTime = System.currentTimeMillis();
			updateGearPath();
		}
	}
	
	public synchronized boolean updateDesiredAngle(){
		if(Dashcomm.get("isVisible", 0) != 0){
			double cameraAngle = Dashcomm.get("angle", 0);
			desiredDistance = Dashcomm.get("distance", 0);
			Translation targetPosition = Translation.fromAngleDistance(desiredDistance, Rotation.fromDegrees(cameraAngle)).rotateBy(Rotation.fromDegrees(Constants.CameraAngleOffset));
			// three inches forward
			Translation offset = new Translation(0, -3);
			desiredAngle = getGyroAngle().rotateBy(offset.getAngleTo(targetPosition).inverse());
			return true;
		} else {
			desiredAngle = getGyroAngle();
			desiredDistance = 0;
			return false;
		}
	}
	
	
	public synchronized void setWheelVelocity(DriveVelocity setVelocity) {
		// inches per sec to rotations per min
		if(setVelocity.wheelSpeed > 216){
			DriverStation.getInstance();
			DriverStation.reportError("Velocity set over 216!", false);
			return;
		}
		// in/s -> (in / pi) * 15
		leftTalon.setSetpoint((setVelocity.wheelSpeed + setVelocity.deltaSpeed) / Math.PI * 15);
		rightTalon.setSetpoint((setVelocity.wheelSpeed - setVelocity.deltaSpeed) / Math.PI * 15);
	
	}

	public synchronized boolean isDone() {
		switch (driveState) {
		case MANUAL:
			return true;
		case AUTO:
			switch(autoState){
			case DRIVING:
				return false;
			case ROTATING:
				return false;
			case DONE:
				return true;
			}
		case GEAR:
			return gearState == GearState.DONE;
		}
		return true;
	}

	private synchronized void updateAutoPath() {
		System.out.println(autoState);
		switch(autoState){
		case DRIVING:
			autoDriveVelocity = autonomousDriver.calculate(RobotTracker.getInstance().getCurrentPosition());
			setWheelVelocity(autoDriveVelocity);
			if(autonomousDriver.isDone(RobotTracker.getInstance().getCurrentPosition())){
				autoState = AutoState.DONE;
			}
			break;
		case ROTATING:
			if(updateRotation()){
				autoState = AutoState.DONE;
			}
			break;
		case DONE:
			setWheelVelocity(new DriveVelocity(0, 0));
		}
	}

	private synchronized boolean updateRotation(){
		Rotation error = desiredAngle.inverse().rotateBy(getGyroAngle());
		//System.out.println(error.getDegrees());
		if (Math.abs(error.getDegrees()) > Constants.DrivingAngleTolerance) {
			double turningSpeed = turningDriver.update(error.getDegrees());
			turningSpeed = OrangeUtility.donut(turningSpeed, 30);
			setWheelVelocity(new DriveVelocity(0, turningSpeed));	
			return false;
		} else {	
			setWheelVelocity(new DriveVelocity(0, 0));
			return true;
		}
	}
	
	private synchronized void updateGearPath() {
		//System.out.println(gearState);
		/*
		System.out.println("error " + Math.abs(desiredAngle - getGyroAngle().getDegrees()));
		System.out.println("desired " + desiredAngle);
		System.out.println("current " + getGyroAngle().getDegrees());
		*/
		
		switch(gearState){
			case TURNING:
				if(true/*gear.isPushed()*/){
 					gearState = GearState.REVERSING;
 				} else if (updateRotation()) {
 					gearState = GearState.DRIVING;
 					System.out.println("DRIVING");
 				} 
				break;
			case DRIVING:

				gearReversingTime = System.currentTimeMillis();
				break;
			case REVERSING:
				gear.setSucking(-0.4);
				setWheelVelocity(new DriveVelocity(Constants.GearSpeed, 0));
				gear.setActuator(-.1);
				/*
				Rotation error = desiredAngle.inverse().rotateBy(getGyroAngle());				
				double turningSpeed = turningDriver.update(error.getDegrees());
				turningSpeed = OrangeUtility.donut(turningSpeed, 11);
				*/
				if(System.currentTimeMillis() - gearReversingTime > 1000){
					gearState = GearState.DONE;
				}
				break;
			case DONE:
				gear.setSucking(0);
				setWheelVelocity(new DriveVelocity(0, 0));
				break;
		}
	}

	public void setBrake(boolean isBraked){
		leftTalon.enableBrakeMode(isBraked);
		rightTalon.enableBrakeMode(isBraked);
		leftSlaveTalon.enableBrakeMode(isBraked);
		rightSlaveTalon.enableBrakeMode(isBraked);
		
	}
	// TODO: Return wheel in inches or something
	public double getLeftDistance() {
		return leftTalon.getPosition() * Constants.WheelDiameter * Math.PI;
	}

	public double getRightDistance() {
		return rightTalon.getPosition() * Constants.WheelDiameter * Math.PI;
	}
	
	public double getDistance() {
		return (getLeftDistance() + getRightDistance()) / 2;
	}
	
	public Rotation getGyroAngle(){
		// -180 through 180
		return Rotation.fromDegrees(gyroSensor.getAngle()).rotateBy(gyroOffset);
	}
	
	public double getAngle(){
		return gyroSensor.getAngle();
	}
	
	public void setOffset(Rotation angleOffset){
		gyroOffset = angleOffset;
	}
	
	public synchronized void arcadeDrive(double moveValue, double rotateValue) {
		if(driveState != DriveState.MANUAL){
			driveState = DriveState.MANUAL;
		}
		double now = Timer.getFPGATimestamp();
		moveValue = scaleJoystickValues(moveValue);
		rotateValue = scaleJoystickValues(rotateValue);
		
		
		double leftMotorSpeed;
		double rightMotorSpeed;

		if (moveValue >= 0.0) {
			moveValue = moveValue * moveValue;
		} else {
			moveValue = -(moveValue * moveValue);
		}
		if (rotateValue >= 0.0) {
			rotateValue = rotateValue * rotateValue;
		} else {
			rotateValue = -(rotateValue * rotateValue);
		}

		if (moveValue > 0.0) {
			if (rotateValue > 0.0) {
				leftMotorSpeed = moveValue - rotateValue;
				rightMotorSpeed = Math.max(moveValue, rotateValue);
			} else {
				leftMotorSpeed = Math.max(moveValue, -rotateValue);
				rightMotorSpeed = moveValue + rotateValue;
			}
		} else {
			if (rotateValue > 0.0) {
				leftMotorSpeed = -Math.max(-moveValue, rotateValue);
				rightMotorSpeed = moveValue + rotateValue;
			} else {
				leftMotorSpeed = moveValue - rotateValue;
				rightMotorSpeed = -Math.max(-moveValue, -rotateValue);
			}
		}

		// 18 ft per sec -> 216 inches per sec
		
		if(shiftState == shiftState.AUTO){
			if(getGear()){
	        	leftMotorSpeed *= 70;
	        	rightMotorSpeed *= 70;
	        	if(Math.abs(getSpeed()) > 70){
	        		shiftUp();
	        		rightTalon.setP(0.1);
	        		rightTalon.setF(0.1453);
	        		leftTalon.setP(0.1);
	        		leftTalon.setF(0.1453);
	        	}
	        } else {
	        	leftMotorSpeed *= 200;
	        	rightMotorSpeed *= 200;
	        	if(Math.abs(getSpeed()) < 55){
	        		shiftDown();
	        		rightTalon.setP(0.3);
	        		rightTalon.setF(0.3923);
	        		leftTalon.setP(0.3);
	        		leftTalon.setF(0.3923);
	        	}
	        }
		}
		
		double dt = (now - lastTime);
		
		double moveSpeed = (leftMotorSpeed + rightMotorSpeed) / 2;
		double turnSpeed = (leftMotorSpeed - rightMotorSpeed) / 2;
		
		double accel = (moveSpeed - lastValue) / dt;
		if(accel < -Constants.MaxAcceleration){
			moveSpeed = lastValue - Constants.MaxAcceleration * dt;
		} else if(accel > Constants.MaxAcceleration){
			moveSpeed = lastValue + Constants.MaxAcceleration * dt;
		}		
		lastTime = now;
		lastValue = moveSpeed;
		setWheelVelocity(new DriveVelocity(moveSpeed, turnSpeed));
	}
	
	public void setShiftState(ShiftState state){
		shiftState = state;
	}
	public void cheesyDrive(double moveValue, double rotateValue, boolean isQuickTurn){
		moveValue = scaleJoystickValues(moveValue);
		rotateValue = scaleJoystickValues(rotateValue);
	
		double leftMotorSpeed;
		double rightMotorSpeed;
		double angularPower = 1;
		
		double overPower;
		
		if(isQuickTurn){
			overPower = 1;
			if(moveValue < 0.2){
				quickStopAccumulator = quickStopAccumulator + rotateValue * 2;
			}
			angularPower = rotateValue;
		} else {
			overPower = 0;
            angularPower = Math.abs(moveValue) * rotateValue - quickStopAccumulator;
            if(quickStopAccumulator > 1){
            	quickStopAccumulator -= 1;
            } else if(quickStopAccumulator < -1){
            	quickStopAccumulator += 1;
            } else {
            	quickStopAccumulator = 0;
            }
		}
		
		leftMotorSpeed = moveValue - angularPower;
		rightMotorSpeed = moveValue + angularPower;
		
        angularPower = Math.abs(moveValue) * rotateValue - quickStopAccumulator;
        
        if (leftMotorSpeed > 1.0) {
            rightMotorSpeed -= overPower * (leftMotorSpeed - 1.0);
            leftMotorSpeed = 1.0;
        } else if (rightMotorSpeed > 1.0) {
            leftMotorSpeed -= overPower * (rightMotorSpeed - 1.0);
            rightMotorSpeed = 1.0;
        } else if (leftMotorSpeed < -1.0) {
            rightMotorSpeed += overPower * (-1.0 - leftMotorSpeed);
            leftMotorSpeed = -1.0;
        } else if (rightMotorSpeed < -1.0) {
            leftMotorSpeed += overPower * (-1.0 - rightMotorSpeed);
            rightMotorSpeed = -1.0;
        }
		leftMotorSpeed *= 216;
		rightMotorSpeed *= 216;
		
		setWheelVelocity(new DriveVelocity((leftMotorSpeed + rightMotorSpeed) / 2, (leftMotorSpeed - rightMotorSpeed) / 2));		
	}
	
	public double getSpeed(){
		return ((leftTalon.getSpeed() + rightTalon.getSpeed()) / 60) * Constants.WheelDiameter * Math.PI;
	}
	/*
	public void drive(double moveValue, double turnValue)
	{
		if (Math.abs(moveValue) >= Constants.MinimumControllerInput) {
			moveValue = (moveValue * (Math.abs(moveValue) - 0.3)) / ((0.7) * Math.abs(moveValue));
		} else {
			moveValue = 0;
		}

		if (Math.abs(turnValue) >= Constants.MinimumControllerInput) {
			turnValue = turnValue * (Math.abs(turnValue) - Constants.MinimumControllerInput)
					/ (Constants.MaximumControllerInput - Constants.MinimumControllerInput) * Math.abs(turnValue);
		} else {
			turnValue = 0;
		}
		
		driveBase.setSafetyEnabled(false);
		driveBase.arcadeDrive(moveValue, turnValue);
		
	}
	*/	 
	
	public void shiftDown(){
		driveShifters.set(true);
	}
	public void shiftUp(){
		driveShifters.set(false);
	}
	
	public boolean getGear(){
		return driveShifters.get();
	}
	
	public void calibrateGyro(){
		gyroSensor.calibrate();
	}
	
	public void zeroSensors(){
		gyroSensor.reset();
		leftTalon.setPosition(0);
		rightTalon.setPosition(0);
	}
	
	public void setNormal(){
		driveMultiplier = 1;
	}
	
	public void setInvert(){
		driveMultiplier = -1;
	}
	
	public void updateConstants(){
		turningDriver.setP(Constants.TurningP);
		turningDriver.setD(Constants.TurningD);
	}
	
	public static class DriveVelocity {
		public double wheelSpeed;
		public double deltaSpeed;
		public DriveVelocity(double wheelSpeed, double deltaSpeed) {
			this.wheelSpeed = wheelSpeed;
			this.deltaSpeed = deltaSpeed;
		}
	}
}
