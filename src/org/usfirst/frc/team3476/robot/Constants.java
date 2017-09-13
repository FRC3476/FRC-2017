package org.usfirst.frc.team3476.robot;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public final class Constants {

	public static double MinimumControllerInput = 0.15;

	// Constants
	// Make these public

	public static double MaximumControllerInput = 1;
	public static double MinimumControllerOutput = 0;
	public static double MaximumControllerOutput = 1;
	public static double MaxAcceleration = 1000;

	public static double LookAheadDistance = 20;
	public static double WheelScrub = 0.8;
	public static double GearSpeed = 50;
	public static double cameraOffset = 5.5;
	public static double TurretCameraOffset = -6.5;// -0.75
	public static double CameraAngleOffset = 0;
	public static double DrivingAngleTolerance = 5;
	public static double DrivingGearTolerance = 10;
	public static double WheelDiameter = 4;
	public static double DriveBaseDiameter = 25;
	public static int LeftMasterDriveId = 2;
	public static int LeftSlaveDriveId = 3;
	public static int RightMasterDriveId = 4;
	public static int RightSlaveDriveId = 5;
	public static int ShifterSolenoidId = 3;
	public static boolean ShifterHighDefault = true;
	// 165 / 70.5
	public static int MasterFlywheelId = 13;
	public static int SlaveFlywheelId = 14;
	public static int LeftBallSensorId = 22;
	public static int RightBallSensorId = 23;
	public static double InitialFlywheelSpeed = 3350;
	public static int IntakeId = -1;

	public static int IntakeFeederId = 7;

	public static int StarFeederId = 6;
	public static int MasterIntakeId = 8; // not used
	public static int SlaveIntakeId = 9;
	public static int IntakeSolenoidId = 0;
	public static int ReverseIntakeSolenoidId = 6;
	public static int GearMechActuatorID = 8;

	public static int GearMechFeederID = 9;
	public static int FuelIntakeId = 18;

	public static int GearSolenoidId = 2;

	public static int PegSensorId = 2;
	public static int GearFlapSolenoidId = 0;
	public static int ClimberId = 10;

	public static int Climber2Id = 17;
	public static double TurretTicksPerRotations = 4096 * (180 / 24);

	public static boolean TurretEnabled = true;
	public static double BoilerHeight = 58;

	public static double yCameraFOV = 38;
	public static double xCameraFOV = 60;
	//
	public static int RightTurretId = 12;
	public static int ServoId = 2;
	public static double TurningP = 0.015;

	public static double TurningD = 0;
	static NetworkTable table = NetworkTable.getTable("SmartDashboard");

	public static void updateConstants() {
	}

	private Constants() {
	}
}