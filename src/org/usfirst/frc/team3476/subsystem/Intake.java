package org.usfirst.frc.team3476.subsystem;
import org.usfirst.frc.team3476.utility.Threaded;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;

public class Intake {

	enum CurrentState {UP,DOWN};

	public static int UP = 1; //replace with some positive number like 1
	public static int DOWN = 0;//replace with some negative number like -1
  
	private Solenoid Lift1;
	private Solenoid Lift2;
	
	CANTalon masterTalon, slaveTalon;
	
	private CurrentState now;
	private CurrentState wanted;
	private double setpoint;
	
	
	public void setSetpoint(double setpoint){
		this.setpoint = setpoint;
		masterTalon.setSetpoint(setpoint);
	}


	private Intake (int Channel1,int Channel2, int masterTalonId, int slaveTalonId) {
	  Lift1 = new Solenoid(Channel1);
	  Lift2 = new Solenoid(Channel2);
	  masterTalon = new CANTalon(masterTalonId);
	  slaveTalon = new CANTalon(slaveTalonId);
	  slaveTalon.changeControlMode(TalonControlMode.Follower);
	  slaveTalon.set(masterTalonId);		
		
	}
	
	public void moveDropDown(boolean position){
		Lift1.set(position);
		Lift2.set(position);
	}
	
	

	@Override
	public void update() {
		// TODO Auto-generated method stub
		if (){
			switch(now){
				case UP:
					moveDropDown(true);
					break;
				case DOWN:
					moveDropDown(false);
					break;	
					}
			}
		
	}

	

}
