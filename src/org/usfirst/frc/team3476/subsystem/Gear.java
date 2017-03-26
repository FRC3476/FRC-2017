package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.subsystem.Intake.IntakeState;
import org.usfirst.frc.team3476.subsystem.OrangeDrive.DriveVelocity;
import org.usfirst.frc.team3476.utility.Constants;
import org.usfirst.frc.team3476.utility.Threaded;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.StatusFrameRate;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class Gear extends Threaded {
	
	public enum GearState {MANUAL, UP, PEG, HOME, DOWN, DONE};
	private GearState currentState;	
	
	double calibrationStartTime;
	
	public static final double UP = -.04, DOWN = -.33, PEG = -.11, HOME = 0; //Default Values, Do not have tick positions
	
	
	private static final Gear gearMechInstance = new Gear();
	
	CANTalon actuatorTalon, gearFeederTalon;
	
	/*private CANTalon ddmotor;
	private PIDController ddController;
	private PowerDistributionPanel pdPanel;
	*/
	
	public static Gear getInstance(){
		return gearMechInstance;
	}
	
	private Gear() {
		  RUNNINGSPEED = 50;
		  gearFeederTalon = new CANTalon(Constants.GearMechFeederID);
		  gearFeederTalon.changeControlMode(TalonControlMode.PercentVbus);
		  
		  actuatorTalon = new CANTalon(Constants.GearMechActuatorID);
		  actuatorTalon.changeControlMode(TalonControlMode.Position);
		  actuatorTalon.configPeakOutputVoltage(2.4, -2.4);
		  actuatorTalon.configNominalOutputVoltage(.7, -.5);
		 
		  actuatorTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		  actuatorTalon.configEncoderCodesPerRev(1024);
		  actuatorTalon.setPosition(0);
		  
		  actuatorTalon.setPID(0.1, 0, 0);
    }
	
	
	public void setPID(double P, double I, double D){
		actuatorTalon.setPID(P, I, D);
	}
	
	public synchronized void setState(GearState state)
	{
		currentState = state;
		update();
	}
	
	public double getPosition(){
		return actuatorTalon.getPosition();	
	}
	
	public synchronized void homeActuator(){
		calibrationStartTime = System.currentTimeMillis();
		currentState = GearState.HOME;
	}
	
	public synchronized GearState getState(){
		return currentState;
	}
	
	public void setSucking(double suck){
		gearFeederTalon.set(suck);
	}
	
	public void setActuator(double val){
		actuatorTalon.changeControlMode(TalonControlMode.PercentVbus);
		actuatorTalon.set(val);
	}
	
	private synchronized void setActuatorPosition(double val){
		actuatorTalon.changeControlMode(TalonControlMode.Position);
		actuatorTalon.setSetpoint(val);
	}
	
	public double getCurrent(){
		return actuatorTalon.getOutputCurrent();
	}

	public double getVoltage() {
		return actuatorTalon.getOutputVoltage();
	}
	
	public void configTalons()
	{
		  actuatorTalon.configPeakOutputVoltage(2.4, -2.4);
		  actuatorTalon.configNominalOutputVoltage(.7, -.5);
	}

	@Override
	public synchronized void update() {
		System.out.println(currentState);
		switch(currentState){
			case MANUAL:
				break;
			case UP:
				setActuatorPosition(UP);
				currentState = GearState.DONE;
				break;
			case PEG:
				setActuatorPosition(PEG);
				currentState = GearState.DONE;
				break;
			case HOME:
				setActuator(0.2);
				if (getCurrent() > 2){
					actuatorTalon.setPosition(HOME);
					System.out.println("HOMED");
					currentState = GearState.DONE;
					setActuator(0);
				} else if (System.currentTimeMillis() - calibrationStartTime > 2000){
					actuatorTalon.setPosition(HOME);
					System.out.println("FAILED TO HOME. USING CURRENT POSITION AS HOME");
					currentState = GearState.DONE;
					setActuator(0);
				}
				break;
			case DOWN:
				setActuator(-0.2);
				if (getCurrent() > 1){
					actuatorTalon.setPosition(DOWN);
					System.out.println("DOWN");
					currentState = GearState.DONE;
					setActuator(0);
				} else if (System.currentTimeMillis() - calibrationStartTime > 2000){
					actuatorTalon.setPosition(DOWN);
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

