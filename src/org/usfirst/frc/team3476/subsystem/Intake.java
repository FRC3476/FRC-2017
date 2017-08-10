package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.utility.Constants;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.Solenoid;

public class Intake {

	public enum IntakeState {
		UP, DOWN
	};

	private static final Intake intakeInstance = new Intake();

	public static Intake getInstance() {
		return Intake.intakeInstance;
	}

	private Solenoid intakeSolenoids;

	CANTalon masterTalon;

	private IntakeState currentState;

	private Intake() {
		intakeSolenoids = new Solenoid(Constants.IntakeSolenoidId);

		masterTalon = new CANTalon(Constants.FuelIntakeId);
		masterTalon.changeControlMode(TalonControlMode.PercentVbus);

	}

	public synchronized IntakeState getState() {
		return currentState;
	}

	public synchronized void setState(IntakeState setState) {
		if (setState == IntakeState.DOWN) {
			intakeSolenoids.set(true);
			currentState = setState;
		} else {
			intakeSolenoids.set(false);
			currentState = setState;
		}
	}

	public void setSucking(double isSucking) {
		masterTalon.set(isSucking);
	}
}