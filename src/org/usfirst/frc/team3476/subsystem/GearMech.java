package org.usfirst.frc.team3476.subsystem;

import org.usfirst.frc.team3476.subsystem.Intake.IntakeState;
import org.usfirst.frc.team3476.subsystem.OrangeDrive.DriveVelocity;
import org.usfirst.frc.team3476.utility.Constants;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.StatusFrameRate;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class GearMech {
	
	public enum GearState {UP,DOWN,PEG};
	private GearState currentState;
	
	private OrangeDrive orangeDrive;
	
	public static final double UP = -.04, DOWN = -.31, PEG = -.11, PEG_EJECT = -.25, HOME = -.005; //Default Values, Do not have tick positions
	
	private static final GearMech gearMechInstance = new GearMech();
	
	CANTalon actuatorTalon, gearFeederTalon;
	
	/*private CANTalon ddmotor;
	private PIDController ddController;
	private PowerDistributionPanel pdPanel;
	*/
	
	public static GearMech getInstance(){
		return gearMechInstance;
	}
	
	private GearMech() {
		  
		  gearFeederTalon = new CANTalon(Constants.GearMechFeederID);
		  gearFeederTalon.changeControlMode(TalonControlMode.PercentVbus);
		  
		  actuatorTalon = new CANTalon(Constants.GearMechActuatorID);
		  actuatorTalon.changeControlMode(TalonControlMode.Position);
		  actuatorTalon.configPeakOutputVoltage(2.4, -2.4);
		 
		  actuatorTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);//
		  actuatorTalon.configEncoderCodesPerRev(1024);
		  
		  actuatorTalon.setPosition(0);
		  orangeDrive = OrangeDrive.getInstance();
    }
	
	
	public void setPID(double P, double I, double D){
		actuatorTalon.setPID(P, I, D);
	}
	
	public double getPosition(){
		return actuatorTalon.getPosition();
		
	}
	
	public synchronized void moveDropDown(GearState setState){
		if(setState == GearState.DOWN) {
			actuatorTalon.setSetpoint(DOWN);
			currentState = setState;
		} else if (setState == GearState.UP){
			actuatorTalon.setSetpoint(UP);
			currentState = setState;
		} else {
			actuatorTalon.setSetpoint(PEG);
			currentState = setState;
		}
	}
	
	public synchronized void manualPegInsert(){
		setActuatorPosition(PEG_EJECT);
		gearFeederTalon.set(-.3);
		//orangeDrive.setWheelVelocity(new DriveVelocity(-10, -10));
		orangeDrive.setManualDrive(-.2, 0);
		long currentTime = System.currentTimeMillis();
		while(System.currentTimeMillis() - currentTime < 2000)
		{
			
		}
		//orangeDrive.setWheelVelocity(new DriveVelocity(0, 0));
		orangeDrive.setManualDrive(0, 0);
		gearFeederTalon.set(0);
		//setActuatorPosition(UP);
	}
	
	public synchronized GearState getState(){
		return currentState;
	}
	
	public void setSucking(double suck){
		gearFeederTalon.set(suck);
	}
	
	public void setActuator(double val)
	{
		actuatorTalon.changeControlMode(TalonControlMode.PercentVbus);
		actuatorTalon.set(val);
	}
	
	public void setActuatorPosition(double val)
	{
		actuatorTalon.changeControlMode(TalonControlMode.Position);
		actuatorTalon.setSetpoint(val);
	}
	
	public double getCurrent()
	{
		return actuatorTalon.getOutputCurrent();
	}
	
	public void resetPosition()
	{
		actuatorTalon.setPosition(0);
	}

	public double getVoltage() {
		return actuatorTalon.getOutputVoltage();
	}
	
	
}
