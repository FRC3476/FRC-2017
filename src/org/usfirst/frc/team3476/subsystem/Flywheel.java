package org.usfirst.frc.team3476.subsystem;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

public class Flywheel {
	
	CANTalon masterTalon, slaveTalon;
	
	private Flywheel(int masterTalonId, int slaveTalonId){
		masterTalon = new CANTalon(masterTalonId);
		slaveTalon = new CANTalon(slaveTalonId);
		slaveTalon.changeControlMode(TalonControlMode.Follower);
		slaveTalon.set(masterTalonId);
		
	}
}
