package org.usfirst.frc.team3476.subsystem;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.DriverStation;

public class Flywheel {

	CANTalon masterTalon, slaveTalon;
	double setpoint;
	double toleranceRange = 50;
	double batVolt;

	private Flywheel(int masterTalonId, int slaveTalonId) {
		masterTalon = new CANTalon(masterTalonId);
		slaveTalon = new CANTalon(slaveTalonId);
		slaveTalon.changeControlMode(TalonControlMode.Follower);
		slaveTalon.set(masterTalonId);

		masterTalon.enableBrakeMode(false);
		slaveTalon.enableBrakeMode(false);

		masterTalon.clearStickyFaults();
		slaveTalon.clearStickyFaults();

		// TODO: Voltage Compensation (Probably change feedforward)
		batVolt = DriverStation.getInstance().getBatteryVoltage();

		/* masterTalon.setP(0.28);
		 * masterTalon.setI(0);
		 * masterTalon.setD(7);
		 * masterTalon.setF(0.0125);
		 * Constants TBD */
	}

	public void setSetpoint(double setpoint) {
		this.setpoint = setpoint;
		masterTalon.setSetpoint(setpoint);
	}

	public void setTolerance(double toleranceRange) {
		this.toleranceRange = toleranceRange;
	}

	public boolean isAtSpeed() {
		return Math.abs(setpoint - masterTalon.getSpeed()) < toleranceRange;
	}

	public double getRpm() {
		return masterTalon.getSpeed();
	}

	public double getSetpoint() {
		return setpoint;
	}

}
