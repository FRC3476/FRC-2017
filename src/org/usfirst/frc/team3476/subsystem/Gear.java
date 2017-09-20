package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.robot.Constants;
import org.usfirst.frc.team3476.utility.Threaded;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

public class Gear extends Threaded {

	public enum GearState {
		MANUAL, UP, PEG, HOME, DOWN, DONE;
	};

	public static final double UP = -.033, DOWN = -.355, PEG = -.082, HOME = 0, PEG_EJECT = -.25; // Default
																									// Values,
																									// Do
																									// not
																									// have
																									// tick
																									// positions

	private static final Gear gearMechInstance = new Gear();

	public static Gear getInstance() {
		return Gear.gearMechInstance;
	}

	private GearState currentState;

	double calibrationStartTime;

	/*
	 * private CANTalon ddmotor; private PIDController ddController; private
	 * PowerDistributionPanel pdPanel;
	 */

	CANTalon actuatorTalon, gearFeederTalon;

	private Gear() {
		gearFeederTalon = new CANTalon(Constants.GearMechFeederID);
		gearFeederTalon.changeControlMode(TalonControlMode.PercentVbus);

		actuatorTalon = new CANTalon(Constants.GearMechActuatorID);
		actuatorTalon.changeControlMode(TalonControlMode.Position);
		actuatorTalon.configPeakOutputVoltage(6, -6);
		actuatorTalon.configNominalOutputVoltage(1.0, 0);

		actuatorTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		actuatorTalon.configEncoderCodesPerRev(1024);
		actuatorTalon.setPosition(0);

		actuatorTalon.setPID(1, 0, 0);

		currentState = GearState.DONE;
	}

	public void configTalons() {
		actuatorTalon.configPeakOutputVoltage(6, -6);
		actuatorTalon.configNominalOutputVoltage(1.0, 0);
	}

	public double getCurrent() {
		return actuatorTalon.getOutputCurrent();
	}

	public double getPosition() {
		return actuatorTalon.getPosition();
	}

	public synchronized GearState getState() {
		return currentState;
	}

	public double getVoltage() {
		return actuatorTalon.getOutputVoltage();
	}

	public double getWheelCurent() {
		return gearFeederTalon.getOutputCurrent();
	}

	public synchronized void homeActuator() {
		calibrationStartTime = System.currentTimeMillis();
		currentState = GearState.HOME;
	}

	public synchronized boolean isDone() {
		return currentState == GearState.DONE;
	}

	public synchronized void manualPegInsert() {
		setActuator(-.15);
		gearFeederTalon.set(-.3);
		// orangeDrive.setWheelVelocity(new DriveVelocity(-10, -10));
		long currentTime = System.currentTimeMillis();
		while (System.currentTimeMillis() - currentTime < 2000) {

		}
		// orangeDrive.setWheelVelocity(new DriveVelocity(0, 0));
		gearFeederTalon.set(0);
		setActuator(0);
		// setActuatorPosition(UP);
	}

	public void setActuator(double val) {
		actuatorTalon.changeControlMode(TalonControlMode.PercentVbus);
		actuatorTalon.set(val);
	}

	public void setActuatorPosition(double val) {
		actuatorTalon.changeControlMode(TalonControlMode.Position);
		actuatorTalon.setSetpoint(val);
	}

	public void setPID(double P, double I, double D) {
		actuatorTalon.setPID(P, I, D);
	}

	public synchronized void setState(GearState state) {
		if (state == GearState.DOWN || state == GearState.HOME) {
			calibrationStartTime = System.currentTimeMillis();
		}
		currentState = state;
	}

	public void setSucking(double suck) {
		gearFeederTalon.set(suck);
	}

	@Override
	public synchronized void update() {
		switch (currentState) {
		case MANUAL:
			break;
		case UP:
			setActuatorPosition(Gear.UP);
			currentState = GearState.DONE;
			break;
		case PEG:
			setActuatorPosition(Gear.PEG);
			currentState = GearState.DONE;
			break;
		case HOME:
			setActuator(0.3);
			if (getCurrent() > 3) {
				actuatorTalon.setPosition(Gear.HOME);
				currentState = GearState.DONE;
				setActuator(0);
			} else if (System.currentTimeMillis() - calibrationStartTime > 1000) {
				actuatorTalon.setPosition(Gear.HOME);
				System.out.println("FAILED TO HOME. USING CURRENT POSITION AS HOME");
				currentState = GearState.DONE;
				setActuator(0);
			}
			break;
		case DOWN:
			setActuator(-0.3);
			// setActuatorPosition(DOWN);
			if (getCurrent() > 3) {
				actuatorTalon.setPosition(Gear.DOWN);
				System.out.println("DOWN");
				currentState = GearState.DONE;
				setActuator(0);
			} else if (System.currentTimeMillis() - calibrationStartTime > 2000) {
				actuatorTalon.setPosition(Gear.DOWN);
				System.out.println("FAILED TO GO DOWN. USING CURRENT POSITION AS DOWN");
				currentState = GearState.DONE;
				setActuator(0);
			}
			break;
		case DONE:
			break;
		}
	}
}
