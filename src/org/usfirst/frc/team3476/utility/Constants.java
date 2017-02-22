package org.usfirst.frc.team3476.utility;

public final class Constants {
	
	private Constants() {
	}

	// Constants
	// Make these public

	public static double MinimumControllerInput = 0.15;
	public static double MaximumControllerInput = 1;
	public static double MinimumControllerOutput = 0;
	public static double MaximumControllerOutput = 1;

	public static double GearAngleTolerance = 2;
	public static double WheelDiameter = 4;
	public static int LeftMasterDriveId = 2;
	public static int LeftSlaveDriveId = 3;
	public static int RightMasterDriveId = 4;
	public static int RightSlaveDriveId = 5;
	public static int ShifterSolenoidId = 3;

	public static int LeftMasterFlywheelId = 34; //CHANGE THE MOTORS IN THE CODE
	public static int LeftSlaveFlywheelId = 35;
	public static int RightMasterFlywheelId = 12;
	public static int RightSlaveFlywheelId = 13;
	public static int LeftBallSensorId = 22;
	public static int RightBallSensorId = 23;
	
	public static int IntakeFeederId = 7;
	public static int UNUSEDFEEDER = 7;
	public static int MasterIntakeId = 8;
	public static int SlaveIntakeId = 9;
	public static int ForwardIntakeSolenoidId = 5;
	public static int ReverseIntakeSolenoidId = 6;

	public static int GearSolenoidId = 2;
	public static int PegSensorId = 1;
	
	public static int ClimberId = 10;
	
	public static double TurretTicksPerRotations = 20000;
	public static int LeftTurretId = 11;
	public static int RightTurretId = 12;
}