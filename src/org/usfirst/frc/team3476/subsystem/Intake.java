package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.utility.Constants;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class Intake {

	public enum IntakeState {UP,DOWN};
  
	private DoubleSolenoid intakeSolenoids;
	
	private static Intake intakeInstance = new Intake();
	
	CANTalon masterTalon, slaveTalon, feederTalon;
	
	private IntakeState currentState;

	public static Intake getInstance() {
		return intakeInstance;
	}
	
	private Intake () {
	  intakeSolenoids = new DoubleSolenoid(Constants.ForwardIntakeSolenoidId, Constants.ReverseIntakeSolenoidId);
	  
	  
	  feederTalon = new CANTalon(Constants.IntakeFeederId);
	  feederTalon.changeControlMode(TalonControlMode.PercentVbus);
	  
	  masterTalon = new CANTalon(Constants.MasterIntakeId);
	  masterTalon.changeControlMode(TalonControlMode.PercentVbus);
	  slaveTalon = new CANTalon(Constants.SlaveIntakeId);
	  slaveTalon.changeControlMode(TalonControlMode.Follower);
	  slaveTalon.set(masterTalon.getDeviceID());
	}
	
	public void setState(IntakeState setState){
		if(setState == IntakeState.DOWN){
			intakeSolenoids.set(Value.kForward);
			currentState = setState;
		} else {
			intakeSolenoids.set(Value.kReverse);
			currentState = setState;
		}
	}
	
	public IntakeState getState(){
		return currentState;
	}
	
	public void setSucking(double isSucking){
		masterTalon.set(isSucking);
	}
	
	public void setFeeder(boolean isFeeding)
	{
		if (isFeeding){
			feederTalon.set(-1);
		} else {
			feederTalon.set(0);
		}
	}
}