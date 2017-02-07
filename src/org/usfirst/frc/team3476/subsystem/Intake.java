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
	
	CANTalon masterTalon, slaveTalon;
	
	private IntakeState currentState;

	private Intake (int Channel1,int Channel2, int masterTalonId, int slaveTalonId) {
	  Lift1 = new Solenoid(Channel1);
	  Lift2 = new Solenoid(Channel2);
	  
	  masterTalon = new CANTalon(masterTalonId);
	  masterTalon.changeControlMode(TalonControlMode.PercentVbus);
	  slaveTalon = new CANTalon(slaveTalonId);
	  
	  slaveTalon.changeControlMode(TalonControlMode.Follower);
	  slaveTalon.set(masterTalonId);				
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
