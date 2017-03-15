package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.subsystem.Intake.IntakeState;
import org.usfirst.frc.team3476.utility.Constants;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.StatusFrameRate;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class GearMech {
	
	public enum GearState {UP,DOWN};
	private GearState currentState;
	
	final double UP = 0, DOWN = 0;//Default Values, Do not have tick positions
	
	private static final GearMech MechInstance = new GearMech();
	
	CANTalon actuatorTalon, gearFeederTalon;
	
	/*private CANTalon ddmotor;
	private PIDController ddController;
	private PowerDistributionPanel pdPanel;
	*/
	
	private GearMech() {
		  
		  gearFeederTalon = new CANTalon(Constants.GearMechFeederID);
		  gearFeederTalon.changeControlMode(TalonControlMode.PercentVbus);
		  
		  actuatorTalon = new CANTalon(Constants.GearMechActuatorID);
		  actuatorTalon.changeControlMode(TalonControlMode.PercentVbus);
		 
		  actuatorTalon.setStatusFrameRateMs(StatusFrameRate.QuadEncoder, 10);//These are all default values so far
		  actuatorTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);//
		  actuatorTalon.configEncoderCodesPerRev(1024);//We don't know the gear ratio yet
		  }
	
	
	public void setPID(double P, double I, double D){
		actuatorTalon.setPID(P, I, D);
	}
	
	
	public synchronized void moveDropDown(GearState setState){
		if(setState == GearState.DOWN){
			actuatorTalon.set(DOWN);
			currentState = setState;
		} else {
			actuatorTalon.set(UP);
			currentState = setState;
		}
	}
	
	public synchronized GearState getState(){
		return currentState;
	}
	
	public void setSucking(double isSucking){
		gearFeederTalon.set(isSucking);
	}
	
	public void setFeeder(boolean isFeeding)
	{
		if (isFeeding){
			gearFeederTalon.set(-1);
		} else {
			gearFeederTalon.set(0);
		}
	}
	
}
