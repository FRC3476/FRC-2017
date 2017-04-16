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
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class Gear extends Threaded {
	
	public enum GearState {MANUAL, UP, PEG, HOME, DOWN, DONE};
	private GearState currentState;	
	
	double calibrationStartTime;

	public static final double UP = -.033, DOWN = -.355, PEG = -.082, HOME = 0, PEG_EJECT = -.25; //Default Values, Do not have tick positions
	
	
	private static final Gear gearMechInstance = new Gear();
	
	private DigitalInput pegSensor;
	
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
		  actuatorTalon.configPeakOutputVoltage(6, -6);
		  actuatorTalon.configNominalOutputVoltage(1.0, 0);
		 
		  actuatorTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		  actuatorTalon.configEncoderCodesPerRev(1024);
		  actuatorTalon.setPosition(0);
		  
		  actuatorTalon.setPID(1.2, 0, 0);
		  
		  pegSensor = new DigitalInput(Constants.PegSensorId);
		  currentState = GearState.DONE;
    }
	
	
	public void setPID(double P, double I, double D){
		actuatorTalon.setPID(P, I, D);
	}
	
	public synchronized void setState(GearState state)
	{
		if (state == GearState.DOWN || state == GearState.HOME)
		{
			calibrationStartTime = System.currentTimeMillis();
		}
		currentState = state;
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
	
	public void setActuatorPosition(double val){
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
		  actuatorTalon.configPeakOutputVoltage(6, -6);
		  actuatorTalon.configNominalOutputVoltage(1.0, 0);
	}
	
	public synchronized void manualPegInsert()
	{
		setActuator(-.15);
		gearFeederTalon.set(-.3);
		//orangeDrive.setWheelVelocity(new DriveVelocity(-10, -10));
		long currentTime = System.currentTimeMillis();
		while(System.currentTimeMillis() - currentTime < 2000)
		{
		 			
		}
		//orangeDrive.setWheelVelocity(new DriveVelocity(0, 0));
		gearFeederTalon.set(0);
		setActuator(0);
		//setActuatorPosition(UP);
	}

	@Override
	public synchronized void update() {
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
				setActuator(0.3);
				if (getCurrent() > 3){
					actuatorTalon.setPosition(HOME);
					System.out.println("HOMED");
					currentState = GearState.DONE;
					setActuator(0);
				} else if (System.currentTimeMillis() - calibrationStartTime > 1000){
					actuatorTalon.setPosition(HOME);
					System.out.println("FAILED TO HOME. USING CURRENT POSITION AS HOME");
					currentState = GearState.DONE;
					setActuator(0);
				}
				break;
			case DOWN:
				setActuator(-0.3);
				//setActuatorPosition(DOWN);
				if (getCurrent() > 3){
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
	
	public boolean isPushed(){
		return !pegSensor.get();
	}
	public synchronized boolean isDone(){
		return currentState == GearState.DONE;
	}

	public double getWheelCurent()
	{
		return gearFeederTalon.getOutputCurrent();
	}
}

