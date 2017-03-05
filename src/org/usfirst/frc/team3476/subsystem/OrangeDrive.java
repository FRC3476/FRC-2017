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

	private ADXRS450_Gyro gyroSensor = new ADXRS450_Gyro(SPI.Port.kOnboardCS0);
	private SynchronousPid turningDriver = new SynchronousPid(0.8, 0, 0, 0);
	private Rotation desiredAngle;
	private boolean isDone;
	private boolean isRotated;
	private double gearReverseTime;

	private double gearSpeed = 100;
	
	private DriveState driveState = DriveState.MANUAL;
	private AutoState autoState;
	private GearState gearState;
	
	private boolean gyroInversed;

	private static final OrangeDrive driveInstance = new OrangeDrive();

	private CANTalon leftTalon, rightTalon;
	//private RobotTracker robotState;
	private PurePursuitController autonomousDriver;
	private DriveVelocity autoDriveVelocity;
	private Solenoid driveShifters = new Solenoid(Constants.ShifterSolenoidId);	
	
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

		CANTalon leftSlaveTalon = new CANTalon(Constants.LeftSlaveDriveId);
		CANTalon rightSlaveTalon = new CANTalon(Constants.RightSlaveDriveId);

		leftSlaveTalon.changeControlMode(TalonControlMode.Follower);
		leftSlaveTalon.set(leftTalon.getDeviceID());
		rightSlaveTalon.changeControlMode(TalonControlMode.Follower);
		rightSlaveTalon.set(rightTalon.getDeviceID());
		configureTalons(TalonControlMode.Speed);
		isRotated = false;
		gyroInversed = false;
		turningDriver.setOutputRange(216, -216);
		turningDriver.setSetpoint(0);
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

	public synchronized void setManualDrive(double moveValue, double turnValue) {
		// low2 + (value - low1) * (high2 - low2) / (high1 - low1)
		if(driveState != DriveState.MANUAL){
			driveState = DriveState.MANUAL;
		}
		if (Math.abs(moveValue) >= Constants.MinimumControllerInput) {
			moveValue = (moveValue * (Math.abs(moveValue) - Constants.MinimumControllerInput)) / ((0.85) * Math.abs(moveValue));
			// 0.7 = Constants.MaximumControllerInput - Constants.MinimumControllerInput
		} else {
			moveValue = 0;
		}

		if (Math.abs(turnValue) >= Constants.MinimumControllerInput) {
			turnValue = turnValue * (Math.abs(turnValue) - Constants.MinimumControllerInput)
					/ (Constants.MaximumControllerInput - Constants.MinimumControllerInput) * Math.abs(turnValue);
		} else {
			turnValue = 0;
		}
		arcadeDrive(moveValue, turnValue);
	}

	public synchronized void setAutoPath(Path autoPath, boolean isReversed) {
		//robotState = RobotTracker.getInstance();
		// PurePursuitController(double lookAheadDistance, double robotSpeed,
		// double robotDiameter, Path robotPath)
		if(driveState != DriveState.AUTO){
			driveState = DriveState.AUTO;
			if(autoState != AutoState.DRIVING){
				autoState = AutoState.DRIVING;					
			}
		}
		autonomousDriver = new PurePursuitController(10, 10, 10, autoPath, isReversed);
		shiftDown();
		updateAutoPath();
		System.out.println("drive");
	}

	public synchronized void setRotation(Rotation desiredRotation){
		if(driveState != DriveState.AUTO){
			driveState = DriveState.AUTO;
			if(autoState != AutoState.ROTATING){
				autoState = AutoState.ROTATING;
			}
		}
		shiftDown();
		desiredAngle = desiredRotation;
		System.out.println("rotating");
	}
	
	public synchronized void setGearPath() {
		if(driveState != DriveState.GEAR){
			driveState = DriveState.GEAR;
			updateDesiredAngle();
			shiftDown();
			gearState = GearState.TURNING;
			updateGearPath();
		}
	}
	
	public synchronized void updateDesiredAngle(){
		if(Dashcomm.get("isVisible", 0) != 0){
			double cameraAngle = Dashcomm.get("angle", 0);
			double distance = Dashcomm.get("distance", 0);
			Translation targetPosition = Translation.fromAngleDistance(distance, Rotation.fromDegrees(cameraAngle));
			Translation offset = new Translation(5.25, -12.25);
			desiredAngle = getGyroAngle().rotateBy(offset.getAngleTo(targetPosition).inverse());
		} else {
			desiredAngle = getGyroAngle();
		}
	}
	
	private synchronized void setWheelVelocity(DriveVelocity setVelocity) {
		// inches per sec to rotations per min
		if(setVelocity.wheelSpeed > 216){
			DriverStation.getInstance();
			DriverStation.reportError("Velocity set over 216!", false);
			return;
		}
		
		leftTalon.setSetpoint((setVelocity.wheelSpeed + setVelocity.deltaSpeed) * 15);
		rightTalon.setSetpoint((setVelocity.wheelSpeed - setVelocity.deltaSpeed) * 15);		
		NetworkTable.getTable("").putNumber("speed", leftTalon.getSpeed());
		NetworkTable.getTable("").putNumber("setpoint", leftTalon.getSetpoint());
		
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
		System.out.println(error.getDegrees());
		if (Math.abs(error.getDegrees()) > Constants.DrivingAngleTolerance) {
			double turningSpeed = turningDriver.update(error.getDegrees());
			turningSpeed = OrangeUtility.donut(turningSpeed, 11);
			setWheelVelocity(new DriveVelocity(0, turningSpeed));	
			return false;
		} else {	
			setWheelVelocity(new DriveVelocity(0, 0));
			return true;
		}
	}
	
	private synchronized void updateGearPath() {
		
		System.out.println(gearState);
		/*
		System.out.println("error " + Math.abs(desiredAngle - getGyroAngle().getDegrees()));
		System.out.println("desired " + desiredAngle);
		System.out.println("current " + getGyroAngle().getDegrees());
		*/
		switch(gearState){
			case TURNING:
				if(Gear.getInstance().isPushed()){
					gearState = GearState.REVERSING;
				} else if (updateRotation()) {
					gearState = GearState.DRIVING;
				} 
				break;
			case DRIVING:
				if (!updateRotation()) {
					gearState = GearState.TURNING;
				} else {
					Rotation error = desiredAngle.inverse().rotateBy(getGyroAngle());
					double turningSpeed = turningDriver.update(error.getDegrees()/2);
					//turningSpeed = OrangeUtility.donut(turningSpeed/2, 10);
					//turningDriver.update(getGyroAngle().getDegrees()));
					setWheelVelocity(new DriveVelocity(-Constants.GearSpeed, turningSpeed));
					if(Gear.getInstance().isPushed()){
						gearState = GearState.REVERSING;
						gearReverseTime = System.currentTimeMillis();
					}
				}
				break;
			case REVERSING:
				setWheelVelocity(new DriveVelocity(Constants.GearSpeed, 0));
				if(System.currentTimeMillis() - gearReverseTime > 500){
					gearState = GearState.DONE;
				}
				break;
			case DONE:
				setWheelVelocity(new DriveVelocity(0, 0));
				break;
		}
	}

	private void configureTalons(TalonControlMode mode) {
		leftTalon.changeControlMode(mode);
		rightTalon.changeControlMode(mode);
	}

	// TODO: Return wheel in inches or something
	public double getLeftDistance() {
		return leftTalon.getPosition() * Constants.WheelDiameter * Math.PI;
	}

	public double getRightDistance() {
		return rightTalon.getPosition() * Constants.WheelDiameter * Math.PI;
	}

	public Rotation getGyroAngle(){
		// -180 through 180
		if(gyroInversed){			
			return Rotation.fromDegrees(gyroSensor.getAngle()).rotateBy(Rotation.fromDegrees(180));
		} else {
			return Rotation.fromDegrees(gyroSensor.getAngle());
		}
	}
	
	public double getAngle(){
		return gyroSensor.getAngle();
	}
	
	public void setInverse(boolean isInversed){
		this.gyroInversed = isInversed;
	}
	
	private void arcadeDrive(double moveValue, double rotateValue) {

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
		leftMotorSpeed *= 216;
		rightMotorSpeed *= 216;
		
		setWheelVelocity(new DriveVelocity((leftMotorSpeed + rightMotorSpeed) / 2, (leftMotorSpeed - rightMotorSpeed) / 2));
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
	
	public void zeroSensors(){
		gyroSensor.calibrate();
		gyroSensor.reset();
		leftTalon.setPosition(0);
		rightTalon.setPosition(0);
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
