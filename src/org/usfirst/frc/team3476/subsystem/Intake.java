package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.utility.Constants;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class Intake {

	public enum IntakeState {UP,DOWN};
  
	private Solenoid intakeSolenoids;
	
	private static final Intake intakeInstance = new Intake();
	
	CANTalon masterTalon;
	
	private IntakeState currentState;

	public static Intake getInstance() {
		return intakeInstance;
	}
	
	private Intake () {
	  intakeSolenoids = new Solenoid(Constants.IntakeSolenoidId);
	  
	  
	  masterTalon = new CANTalon(Constants.FuelIntakeId);
	  masterTalon.changeControlMode(TalonControlMode.PercentVbus);
	  
	}
	
	public synchronized void setState(IntakeState setState){
		if(setState == IntakeState.DOWN){
			intakeSolenoids.set(true);
			currentState = setState;
		} else {
			intakeSolenoids.set(false);
			currentState = setState;
		}
	}
	
	public synchronized IntakeState getState(){
		return currentState;
	}
	
	public void setSucking(double isSucking){
		masterTalon.set(isSucking);
	}
}