package org.usfirst.frc.team3476.utility;

public final class Constants
{	
	private Constants(){
		updateConstants();
	}
	
	//Constants
	//Make these public
	
	public static double MinimumControllerInput;
	public static double MaximumControllerInput;
	public static double MinimumControllerOutput;
	public static double MaximumControllerOutput; 
	
	public static double GearAngleTolerance;
	public static double WheelDiameter; 
	public static int LeftMasterDriveId; 
	public static int LeftSlaveDriveId; 
	public static int RightMasterDriveId; 
	public static int RightSlaveDriveId; 
	
	public static int LeftMasterFlywheelId;
	public static int LeftSlaveFlywheelId;
	public static int RightMasterFlywheelId;
	public static int RightSlaveFlywheelId;
	public static int LeftBallSensorId;
	public static int RightBallSensorId;
	
	public static double TurretTicksPerRotations;
	
	public static void updateConstants()
	{
		MinimumControllerInput = Dashcomm.get("Constants/MinimumControllerInput", 0.3);
		MaximumControllerInput = Dashcomm.get("Constants/MaximumControllerInput", 1);
		MinimumControllerOutput = Dashcomm.get("Constants/MinimumControllerOutput", 0);
		MaximumControllerOutput = Dashcomm.get("Constants/MaximumControllerOutput", 1); 
		
		GearAngleTolerance = Dashcomm.get("Constants/GearAngleTolerance", 2);
		WheelDiameter = Dashcomm.get("Constants/WheelDiameter", 4); 
		LeftMasterDriveId = (int) Dashcomm.get("Constants/LeftMasterDriveId", 4); 
		LeftSlaveDriveId = (int) Dashcomm.get("Constants/LeftSlaveDriveId", 5); 
		RightMasterDriveId = (int) Dashcomm.get("Constants/RightMasterDriveId", 2); 
		RightSlaveDriveId = (int) Dashcomm.get("Constants/RightSlaveDriveId", 3);		
		LeftBallSensorId = (int) Dashcomm.get("Constants/LeftBallSensorId", 22);
		RightBallSensorId = (int) Dashcomm.get("Constants/RightBallSensorId", 23);
		
		
		LeftMasterFlywheelId = 10;//(int) Dashcomm.get("Constants/LeftMasterFlywheelId", 10);
		LeftSlaveFlywheelId = 11;//(int) Dashcomm.get("Constants/LeftSlaveFlywheelId", 11);
		RightMasterFlywheelId = (int) Dashcomm.get("Constants/RightMasterFlywheelId", 0);
		RightSlaveFlywheelId = (int) Dashcomm.get("Constants/RightSlaveFlywheelId", 0);
		
		TurretTicksPerRotations = Dashcomm.get("Constants/TurretTicksPerRotations", 20000);
	}
}