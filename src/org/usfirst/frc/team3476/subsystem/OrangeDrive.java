package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.utility.Constants;
import org.usfirst.frc.team3476.utility.Dashcomm;
import org.usfirst.frc.team3476.utility.Path;
import org.usfirst.frc.team3476.utility.PurePursuitController;
import org.usfirst.frc.team3476.utility.SynchronousPid;
import org.usfirst.frc.team3476.utility.Threaded;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.StatusFrameRate;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.RobotDrive;

/* Inspiration from Team 254 */

public class OrangeDrive extends Threaded {
	public enum DriveState {
		MANUAL, AUTO, GEAR
	}

	private DriveState driveState = DriveState.MANUAL;

	private double desiredAngle;

	private boolean isDone;

	private RobotDrive driveBase;
	private AnalogGyro testGyro = new AnalogGyro(0);
	private CANTalon leftTalon, rightTalon;
	private RobotTracker robotState = RobotTracker.getInstance();
	private PurePursuitController autonomousDriver;
	private SynchronousPid gearDriver = new SynchronousPid(0.1, 0.01, 0, 0.1);
	private DriveVelocity autoDriveVelocity;
	private static OrangeDrive driveInstance = new OrangeDrive();

	// Do not do private static double MINIMUM_INPUT = Constants.MinimumControllerInput;
	// just use Constants.X

	public static OrangeDrive getInstance() {
		return driveInstance;
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
		leftSlaveTalon.set(Constants.LeftMasterDriveId);
		rightSlaveTalon.changeControlMode(TalonControlMode.Follower);
		rightSlaveTalon.set(Constants.RightMasterDriveId);

		configureTalons(TalonControlMode.Speed);
	}

	@Override
	public void update() {
		switch (driveState) {
		case MANUAL:
			// check for last setArcadeDrive
			break;
		case AUTO:
			updateAutoPath();
			break;
		case GEAR:
			updateGearPath();
			break;
		}
	}

	public void setManualDrive(double moveValue, double turnValue) {
		if (driveState != DriveState.MANUAL) {
			driveState = DriveState.MANUAL;
			configureTalons(TalonControlMode.Speed);
		}
		// low2 + (value - low1) * (high2 - low2) / (high1 - low1)
		if (Math.abs(moveValue) >= Constants.MinimumControllerInput) {
			// moveValue = (moveValue * (Math.abs(moveValue) - Constants.MinimumControllerInput)) /
			// ((Constants.MaximumControllerInput - Constants.MinimumControllerInput) * Math.abs(moveValue));
			moveValue = (moveValue * (Math.abs(moveValue) - 0.3)) / ((0.7) * Math.abs(moveValue));
			// moveValue * (MinimumControllerOutput + (Math.abs(moveValue) - MinimumControllerInput) *
			// (MaximumControllerOutput - MinimumControllerOutput)) / (MaximumControllerInput - MinimumControllerInput)
			// * Math.abs(moveValue);
			// ^ is the correct way but we can take out MinimumControllerOutput in the front because it will be 0 and
			// also the (MaximumControllerOutput - MinimumControllerOutput) because that will amount to 1
		}

		if (Math.abs(turnValue) >= Constants.MinimumControllerInput) {
			turnValue = turnValue * (Math.abs(turnValue) - Constants.MinimumControllerInput)
					/ (Constants.MaximumControllerInput - Constants.MinimumControllerInput) * Math.abs(turnValue);
		}

		// System.out.println("left " + leftTalon.getSpeed() + "right " + rightTalon.getSpeed());
		arcadeDrive(moveValue, turnValue);
	}

	public void setAutoPath(Path autoPath) {
		if (driveState != DriveState.AUTO) {
			driveState = DriveState.AUTO;
			configureTalons(TalonControlMode.Speed);
		}
		// PurePursuitController(double lookAheadDistance, double robotSpeed,
		// double robotDiameter, Path robotPath)
		autonomousDriver = new PurePursuitController(10, 10, 10, autoPath);
		updateAutoPath();
	}

	public void setGearPath() {
		if (driveState != DriveState.GEAR) {
			driveState = DriveState.GEAR;
			configureTalons(TalonControlMode.Speed);
		}

		isDone = false;
		desiredAngle = testGyro.getAngle() + Dashcomm.get("angle", 0);
		gearDriver.setSetpoint(desiredAngle);
		updateGearPath();
	}

	private void setWheelVelocity(DriveVelocity setVelocity) {
		// inches per sec to rotations per min
		leftTalon.setSetpoint((setVelocity.wheelSpeed + setVelocity.deltaSpeed) * 15);
		rightTalon.setSetpoint((setVelocity.wheelSpeed - setVelocity.deltaSpeed) * 15);
		System.out.println("setpoint " + setVelocity.wheelSpeed);
		System.out.println("delta " + setVelocity.deltaSpeed);
	}

	public boolean isDone() {
		switch (driveState) {
		case MANUAL:
			return true;
		case AUTO:
			// check if path is completed
			return autonomousDriver.isDone(robotState.getCurrentPosition());
		case GEAR:
			return isDone;
		}
		return true;
	}

	private void updateAutoPath() {
		autoDriveVelocity = autonomousDriver.calculate(robotState.getCurrentPosition());
		setWheelVelocity(autoDriveVelocity);
	}

	public void updateGearPath() {
		if (Math.abs(desiredAngle - testGyro.getAngle()) > Constants.GearAngleTolerance) {
			setWheelVelocity(new DriveVelocity(0, gearDriver.update(testGyro.getAngle())));
		} else {
			DriveVelocity drivingSpeed = new DriveVelocity(10, 0);
			setWheelVelocity(drivingSpeed);
		}
	}

	public void configureTalons(TalonControlMode mode) {
		leftTalon.changeControlMode(mode);
		rightTalon.changeControlMode(mode);
	}

	// TODO: Return wheel in inches or something
	public double getLeftDistance() {
		return leftTalon.getPosition();
	}

	public double getRightDistance() {
		return rightTalon.getPosition();
	}

	public void arcadeDrive(double moveValue, double rotateValue) {

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

		setWheelVelocity(new DriveVelocity((leftMotorSpeed + rightMotorSpeed) / 2, (leftMotorSpeed - rightMotorSpeed)
				/ 2));
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
