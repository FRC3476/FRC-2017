package org.usfirst.frc.team3476.subsystem;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.Solenoid;

public class Intake {

	enum IntakeState {UP,DOWN};

	public static int UP = 1; //replace with some positive number like 1
	public static int DOWN = 0;//replace with some negative number like -1
  
	private Solenoid Lift1;
	private Solenoid Lift2;
	
	private static Intake intakeInstance = new Intake();
	
	CANTalon masterTalon, slaveTalon;
	
	private IntakeState currentState;

	public static Intake getInstance() {
		return intakeInstance;
	}
	
	private Intake () {
	  Lift1 = new Solenoid(Constants.Solenoid1);
	  Lift2 = new Solenoid(Constants.Solenoid2);
	  
	  masterTalon = new CANTalon(Constants.IntakeMasterId);
	  masterTalon.changeControlMode(TalonControlMode.PercentVbus);
	  slaveTalon = new CANTalon(Constants.IntakeSlaveId);
	  
	  slaveTalon.changeControlMode(TalonControlMode.Follower);
	  slaveTalon.set(Constants.IntakeMasterId);
	}
	
	public void setState(IntakeState setState){
		if(setState == IntakeState.DOWN){
			Lift1.set(false);
			Lift2.set(false);
			currentState = setState;
		} else {
			Lift1.set(true);
			Lift2.set(true);
			currentState = setState;
		}
	}
	
	public IntakeState getState(){
		return currentState;
	}
	
	public void setSucking(boolean isSucking){
		if(isSucking){
			masterTalon.set(1);
		} else {
			masterTalon.set(0);
		}
	}
}
