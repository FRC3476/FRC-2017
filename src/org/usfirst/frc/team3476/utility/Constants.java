package org.usfirst.frc.team3476.utility;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public final class Constants {
	
	private Constants() {
	}

	// Constants
	// Make these public

	public static double MinimumControllerInput = 0.15;
	public static double MaximumControllerInput = 1;
	public static double MinimumControllerOutput = 0;
	public static double MaximumControllerOutput = 1;

	public static double GearSpeed = 25;
	public static double cameraOffset = 5.5;
	public static double CameraAngleOffset = 0;
	public static double DrivingAngleTolerance = 10;
	public static double WheelDiameter = 4;
	public static double DriveBaseDiameter = 25;
	public static int LeftMasterDriveId = 2;
	public static int LeftSlaveDriveId = 3;
	public static int RightMasterDriveId = 4;
	public static int RightSlaveDriveId = 5;
	public static int ShifterSolenoidId = 3;

	public static int LeftMasterFlywheelId = 15;
	public static int LeftSlaveFlywheelId = 16; 
	public static int RightMasterFlywheelId = 13;
	public static int RightSlaveFlywheelId = 14; 
	public static int LeftBallSensorId = 22;
	public static int RightBallSensorId = 23;
	
	public static int IntakeFeederId = 7; 
	public static int StarFeederId = 6;
	public static int MasterIntakeId = 16;
	public static int SlaveIntakeId = 9;
	public static int ForwardIntakeSolenoidId = 5;
	public static int ReverseIntakeSolenoidId = 6;

	public static int GearSolenoidId = 2;
	public static int PegSensorId = 2;
	public static int GearFlapSolenoidId = 0;
	
	public static int ClimberId = 10;  
	
	public static int Climber2Id = 8; 
	
	public static double TurretTicksPerRotations = 20000;
	public static int LeftTurretId = 11; // change
	public static int RightTurretId = 12; // change
	
	public static double TurningP = 0.02;
	public static double TurningD = 0.18;
	static NetworkTable table = NetworkTable.getTable("SmartDashboard");
	
	public static void updateConstants(){
	}
}