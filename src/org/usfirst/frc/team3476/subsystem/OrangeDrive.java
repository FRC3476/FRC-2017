package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.robot.Constants;
import org.usfirst.frc.team3476.subsystem.Gear.GearState;
import org.usfirst.frc.team3476.utility.Dashcomm;
import org.usfirst.frc.team3476.utility.Path;
import org.usfirst.frc.team3476.utility.PurePursuitController;
import org.usfirst.frc.team3476.utility.Rotation;
import org.usfirst.frc.team3476.utility.SynchronousPid;
import org.usfirst.frc.team3476.utility.Threaded;
import org.usfirst.frc.team3476.utility.Translation;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.StatusFrameRate;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;

/* Inspiration from Team 254 */

public class OrangeDrive extends Threaded {
	public enum AutoState {
		TIMED, DRIVING, ROTATING, DONE
	}

	public enum DriveState {
		MANUAL, AUTO, GEAR
	}

	public static class DriveVelocity {
		/*
		 * Inches per second for speed
		 */
		public double wheelSpeed;
		public double deltaSpeed;

		public DriveVelocity(double wheelSpeed, double deltaSpeed) {
			this.wheelSpeed = wheelSpeed;
			this.deltaSpeed = deltaSpeed;
		}
	}

	public enum GearDrivingState {
		TURNING, DRIVING, REVERSING, DONE
	}

	public enum ShiftState {
		MANUAL, AUTO
	}

	private static final OrangeDrive instance = new OrangeDrive();

	public static OrangeDrive getInstance() {
		return OrangeDrive.instance;
	}

	private double quickStopAccumulator;
	private double lastTime;
	private double lastValue;

	private double gearReversingTime;
	private double gearDrivingTime;
	private double gearStartTime;

	private double driveStartTime;
	private double driveTime;

	private double driveMultiplier;
	private boolean dontShiftDown = false;
	private boolean drivePercentVbus = false;

	private ADXRS450_Gyro gyroSensor = new ADXRS450_Gyro(SPI.Port.kOnboardCS0);

	private SynchronousPid turningDriver = new SynchronousPid(Constants.TurningP, 0, Constants.TurningD, 0);
	private Rotation desiredAngle;
	private Rotation gyroOffset;
	private ShiftState shiftState = ShiftState.AUTO;

	private DriveState driveState = DriveState.MANUAL;
	private AutoState autoState;

	private GearDrivingState gearState;
	private Gear gear = Gear.getInstance();
	private CANTalon leftTalon, rightTalon, leftSlaveTalon, rightSlaveTalon;
	private PurePursuitController autonomousDriver;

	private DriveVelocity autoDriveVelocity;

	private Solenoid driveShifters = new Solenoid(Constants.ShifterSolenoidId);

	private OrangeDrive() {
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

		turningDriver.setOutputRange(200, -200);
		turningDriver.setSetpoint(0);
		gyroOffset = Rotation.fromDegrees(0);
		shiftUp();
	}

	public synchronized void arcadeDrive(double moveValue, double rotateValue) {
		if (driveState != DriveState.MANUAL) {
			driveState = DriveState.MANUAL;
		}
		moveValue = scaleJoystickValues(moveValue);
		rotateValue = scaleJoystickValues(rotateValue);

		double leftMotorSpeed;
		double rightMotorSpeed;
		// Square values but keep sign
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
		// Get highest correct speed for left/right wheels
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

		// Units in in/s
		// Shift if the current speed is over a certain point
		// 90 % of low gear speed
		if (shiftState == ShiftState.AUTO) {
			if (getGear()) {
				if (Math.abs(getSpeed()) > 56) {
					shiftUp();
				}
			} else {
				if (Math.abs(getSpeed()) < 45) {
					shiftDown();
				}
			}
		}

		if (drivePercentVbus) {
			double moveSpeed = (leftMotorSpeed + rightMotorSpeed) / 2;
			double turnSpeed = (leftMotorSpeed - rightMotorSpeed) / 2;
			setWheelPower(new DriveVelocity(moveSpeed, turnSpeed));
		} else {

			leftMotorSpeed *= driveMultiplier;
			rightMotorSpeed *= driveMultiplier;

			// get acceleration
			// assumes that wheel speed pid works
			// works by limiting how much higher/lower we can set the speed
			double now = Timer.getFPGATimestamp();
			double dt = (now - lastTime);

			double moveSpeed = (leftMotorSpeed + rightMotorSpeed) / 2;
			double turnSpeed = (leftMotorSpeed - rightMotorSpeed) / 2;

			double accel = (moveSpeed - lastValue) / dt;
			if (accel < -Constants.MaxAcceleration) {
				moveSpeed = lastValue - Constants.MaxAcceleration * dt;
			} else if (accel > Constants.MaxAcceleration) {
				moveSpeed = lastValue + Constants.MaxAcceleration * dt;
			}

			lastTime = now;
			lastValue = moveSpeed;
			setWheelVelocity(new DriveVelocity(moveSpeed, turnSpeed));
		}
	}

	public void calibrateGyro() {
		gyroSensor.calibrate();
	}

	public void cheesyDrive(double moveValue, double rotateValue, boolean isQuickTurn) {
		moveValue = scaleJoystickValues(moveValue);
		rotateValue = scaleJoystickValues(rotateValue);

		double leftMotorSpeed;
		double rightMotorSpeed;
		double angularPower = 1;

		double overPower;

		if (isQuickTurn) {
			overPower = 1;
			if (moveValue < 0.2) {
				quickStopAccumulator = quickStopAccumulator + rotateValue * 2;
			}
			angularPower = rotateValue;
		} else {
			overPower = 0;
			angularPower = Math.abs(moveValue) * rotateValue - quickStopAccumulator;
			if (quickStopAccumulator > 1) {
				quickStopAccumulator -= 1;
			} else if (quickStopAccumulator < -1) {
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
		leftMotorSpeed *= driveMultiplier;
		rightMotorSpeed *= driveMultiplier;

		setWheelVelocity(
				new DriveVelocity((leftMotorSpeed + rightMotorSpeed) / 2, (leftMotorSpeed - rightMotorSpeed) / 2));
	}

	public double getAngle() {
		return gyroSensor.getAngle();
	}

	public double getDistance() {
		return (getLeftDistance() + getRightDistance()) / 2;
	}

	public boolean getGear() {
		return driveShifters.get();
	}

	public synchronized GearDrivingState getGearState() {
		return gearState;
	}

	public Rotation getGyroAngle() {
		// -180 through 180
		return Rotation.fromDegrees(gyroSensor.getAngle()).rotateBy(gyroOffset);
	}

	// TODO: Return wheel in inches or something
	public double getLeftDistance() {
		return leftTalon.getPosition() * Constants.WheelDiameter * Math.PI;
	}

	public double getRightDistance() {
		return rightTalon.getPosition() * Constants.WheelDiameter * Math.PI;
	}

	public double getSpeed() {
		return ((leftTalon.getSpeed() + rightTalon.getSpeed()) / 120) * Constants.WheelDiameter * Math.PI;
	}

	public synchronized DriveState getState() {
		return driveState;
	}

	public synchronized boolean isDone() {
		switch (driveState) {
		case MANUAL:
			return true;
		case AUTO:
			switch (autoState) {
			case TIMED:
				return false;
			case DRIVING:
				return false;
			case ROTATING:
				return false;
			case DONE:
				return true;
			}
		case GEAR:
			return gearState == GearDrivingState.DONE;
		}
		return true;
	}

	public void resetGyro() {
		gyroSensor.reset();
	}

	public void resetState() {
		driveState = DriveState.MANUAL;
		gearState = GearDrivingState.DONE;
	}

	public double scaleJoystickValues(double rawValue) {
		return scaleValues(rawValue, Constants.MinimumControllerInput, Constants.MaximumControllerInput,
				Constants.MinimumControllerOutput, Constants.MaximumControllerOutput);
	}

	public double scaleValues(double rawValue, double minInput, double maxInput, double minOutput, double maxOutput) {
		// scales ranges IE. 0.15 - 1 to 0 - 1
		// the absolute value of the rawValue under minimum are treated as 0
		// negative values also work
		// values higher than maxInput returns maxOutput
		if (Math.abs(rawValue) >= minInput) {
			double norm = (rawValue - minInput) / (maxInput - minInput);
			norm = norm * (maxOutput - minOutput) + ((norm * minOutput) / Math.abs(norm));
			return norm;
		} else {
			return 0;
		}
	}

	public synchronized void setAutoPath(Path autoPath, boolean isReversed) {
		// robotState = RobotTracker.getInstance();
		// PurePursuitController(double lookAheadDistance, double robotSpeed,
		// double robotDiameter, Path robotPath)
		if (driveState != DriveState.AUTO) {
			driveState = DriveState.AUTO;
			setBrakeState(true);
			shiftUp();
		}

		if (autoState != AutoState.DRIVING) {
			autoState = AutoState.DRIVING;
		}
		autonomousDriver = new PurePursuitController(autoPath, isReversed);
		updateAutoPath();
		// System.out.println("drive");
	}

	public synchronized void setAutoTime(double speed, double time) {
		driveState = DriveState.AUTO;
		setBrakeState(true);
		shiftUp();
		autoState = AutoState.TIMED;
		driveTime = time;
		driveStartTime = System.currentTimeMillis();
		setWheelVelocity(new DriveVelocity(speed, 0));
	}

	public void setBrakeState(boolean isBraked) {
		leftTalon.enableBrakeMode(isBraked);
		rightTalon.enableBrakeMode(isBraked);
		leftSlaveTalon.enableBrakeMode(isBraked);
		rightSlaveTalon.enableBrakeMode(isBraked);
	}

	public synchronized void setGearPath() {
		if (driveState != DriveState.GEAR) {
			driveState = DriveState.GEAR;
			gearState = GearDrivingState.TURNING;
			setBrakeState(true);
			shiftUp();
			updateAngleToPeg();
			gear.setState(GearState.PEG);
			updateGearPath();
		}
	}

	public void setInvert() {
		driveMultiplier = -1;
	}

	public synchronized void setManualGearPath() {
		if (driveState != DriveState.GEAR) {
			driveState = DriveState.GEAR;
			gearState = GearDrivingState.REVERSING;
			setBrakeState(true);
			shiftUp();
			gearReversingTime = System.currentTimeMillis();
			updateGearPath();
		}
	}

	public void setNormal() {
		driveMultiplier = 1;
	}

	public void setOffset(Rotation angleOffset) {
		gyroOffset = angleOffset;
	}

	public synchronized void setRotation(Rotation desiredRotation) {
		driveState = DriveState.AUTO;
		setBrakeState(true);
		shiftUp();

		if (autoState != AutoState.ROTATING) {
			autoState = AutoState.ROTATING;
		}
		desiredAngle = desiredRotation;
	}

	public void setShiftState(ShiftState state) {
		shiftState = state;
	}

	private void setWheelPower(DriveVelocity setVelocity) {
		leftTalon.changeControlMode(TalonControlMode.PercentVbus);
		rightTalon.changeControlMode(TalonControlMode.PercentVbus);
		leftTalon.set(setVelocity.wheelSpeed + setVelocity.deltaSpeed);
		// power is reversed for right side
		rightTalon.set(-(setVelocity.wheelSpeed - setVelocity.deltaSpeed));
	}

	private void setWheelVelocity(DriveVelocity setVelocity) {
		leftTalon.changeControlMode(TalonControlMode.Speed);
		rightTalon.changeControlMode(TalonControlMode.Speed);
		// inches per sec to rotations per min
		if (setVelocity.wheelSpeed > 216) {
			DriverStation.getInstance();
			DriverStation.reportError("Velocity set over 216!", false);
			return;
		}
		// in/s -> (in / pi) * 15
		leftTalon.setSetpoint((setVelocity.wheelSpeed + setVelocity.deltaSpeed) / Math.PI * 15);
		rightTalon.setSetpoint((setVelocity.wheelSpeed - setVelocity.deltaSpeed) / Math.PI * 15);

	}

	public synchronized void shiftDown() {
		if (dontShiftDown) {
			shiftUp();
		} else {
			driveMultiplier = 70;
			driveShifters.set(Constants.ShifterHighDefault);
			rightTalon.setP(0.3);
			rightTalon.setF(0.3923);
			leftTalon.setP(0.3);
			leftTalon.setF(0.3923);
		}
	}

	public synchronized void shiftUp() {
		driveMultiplier = 200;
		driveShifters.set(!Constants.ShifterHighDefault);
		rightTalon.setP(0.2); // 0.45 on practice
		rightTalon.setF(0.1453);
		leftTalon.setP(0.2);
		leftTalon.setF(0.1453);
	}

	public synchronized void toggleSimpleDrive() {
		if (drivePercentVbus) {
			drivePercentVbus = false;
		} else {
			drivePercentVbus = true;
		}
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

	private synchronized void updateAutoPath() {
		switch (autoState) {
		case TIMED:
			if (System.currentTimeMillis() - driveStartTime > driveTime) {
				setWheelVelocity(new DriveVelocity(0, 0));
				autoState = AutoState.DONE;
			}
			break;
		case DRIVING:
			autoDriveVelocity = autonomousDriver.calculate(RobotTracker.getInstance().getOdometry());
			setWheelVelocity(autoDriveVelocity);
			if (autonomousDriver.isDone(RobotTracker.getInstance().getOdometry())) {
				autoState = AutoState.DONE;
			}
			break;
		case ROTATING:
			if (updateRotation()) {
				autoState = AutoState.DONE;
			}
			break;
		case DONE:
			setWheelVelocity(new DriveVelocity(0, 0));
		}
	}

	public void updateConstants() {
		turningDriver.setP(Constants.TurningP);
		turningDriver.setD(Constants.TurningD);
	}

	public synchronized boolean updateAngleToPeg() {
		double cameraAngle = VisionServer.getInstance().getGearData().getAngle();
		double desiredDistance = VisionServer.getInstance().getGearData().getDistance();
		gearDrivingTime = (desiredDistance / Constants.GearSpeed) + 0.5;
		Translation targetPosition = Translation
				.fromAngleDistance(desiredDistance, Rotation.fromDegrees(cameraAngle))
				.rotateBy(Rotation.fromDegrees(Constants.CameraAngleOffset));
		Translation offset = new Translation(0.5, -9.5);
		desiredAngle = getGyroAngle().rotateBy(offset.getAngleTo(targetPosition).inverse());
		return true;
	}

	private synchronized void updateGearPath() {
		switch (gearState) {
		case TURNING:
			if (updateRotation()) {
				gearStartTime = System.currentTimeMillis();
				gearState = GearDrivingState.DRIVING;
			}
			break;
		case DRIVING:
			Rotation error = desiredAngle.inverse().rotateBy(getGyroAngle());
			// System.out.println("error" + error.getDegrees());
			double turningSpeed = turningDriver.update(error.getDegrees());
			turningSpeed = scaleValues(turningSpeed, 0, 1, 5, 200);
			setWheelVelocity(new DriveVelocity(-Constants.GearSpeed, turningSpeed));
			if (System.currentTimeMillis() - gearStartTime > gearDrivingTime * 1000) {
				gearReversingTime = System.currentTimeMillis();
				gearState = GearDrivingState.REVERSING;
			}
			break;
		case REVERSING:
			gear.setSucking(-0.4);
			setWheelVelocity(new DriveVelocity(20, 0));
			gear.setActuator(-.3);
			/*
			 * Rotation error = desiredAngle.inverse().rotateBy(getGyroAngle());
			 * double turningSpeed = turningDriver.update(error.getDegrees());
			 * turningSpeed = OrangeUtility.donut(turningSpeed, 11);
			 */
			if (System.currentTimeMillis() - gearReversingTime > 1300) {
				gearState = GearDrivingState.DONE;
			}
			break;
		case DONE:
			gear.setSucking(0);
			setWheelVelocity(new DriveVelocity(0, 0));
			break;
		}
	}

	public void updatePIDF(double P, double I, double D, double F) {
		turningDriver.setPIDF(P, I, D, F);
	}

	private synchronized boolean updateRotation() {
		Rotation error = desiredAngle.inverse().rotateBy(getGyroAngle());
		if (Math.abs(error.getDegrees()) > Constants.DrivingAngleTolerance) {
			double turningSpeed = turningDriver.update(error.getDegrees());
			turningSpeed = scaleValues(turningSpeed, 0, 1, 20, 100);
			setWheelVelocity(new DriveVelocity(0, turningSpeed));
			return false;
		} else {
			setWheelVelocity(new DriveVelocity(0, 0));
			return true;
		}
	}

	public void zeroSensors() {
		gyroSensor.reset();
		leftTalon.setPosition(0);
		rightTalon.setPosition(0);
	}
}
