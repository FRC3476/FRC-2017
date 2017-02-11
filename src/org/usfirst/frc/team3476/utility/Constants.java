package org.usfirst.frc.team3476.utility;

public final class Constants {
	private Constants() {
	}

	// Constants
	// Make these public

	public static double MINIMUM_INPUT = Dashcomm.get("Constants/MINIMUM_INPUT", 0);
	public static double MAXIMUM_INPUT = Dashcomm.get("Constants/MAXIMUM_INPUT", 0);
	public static double MINIMUM_OUTPUT = Dashcomm.get("Constants/MINIMUM_OUTPUT", 0);
	public static double MAXIMUM_OUTPUT = Dashcomm.get("Constants/MAXIMUM_OUTPUT", 0);
	public static double WHEEL_DIAMETER = Dashcomm.get("Constants/WHEEL_DIAMETER", 0);
	public static int LEFT_MOTOR = (int) Dashcomm.get("Constants/LEFT_MOTOR", 0);
	public static int LEFT_SLAVE = (int) Dashcomm.get("Constants/LEFT_SLAVE", 0);
	public static int RIGHT_MOTOR = (int) Dashcomm.get("Constants/RIGHT_MOTOR", 0);
	public static int RIGHT_SLAVE = (int) Dashcomm.get("Constants/RIGHT_SLAVE", 0);

	public static void updateConstants() {
		MINIMUM_INPUT = Dashcomm.get("Constants/MINIMUM_INPUT", 0);
		MAXIMUM_INPUT = Dashcomm.get("Constants/MAXIMUM_INPUT", 0);
		MINIMUM_OUTPUT = Dashcomm.get("Constants/MINIMUM_OUTPUT", 0);
		MAXIMUM_OUTPUT = Dashcomm.get("Constants/MAXIMUM_OUTPUT", 0);
		WHEEL_DIAMETER = Dashcomm.get("Constants/WHEEL_DIAMETER", 0);
		LEFT_MOTOR = (int) Dashcomm.get("Constants/LEFT_MOTOR", 0);
		LEFT_SLAVE = (int) Dashcomm.get("Constants/LEFT_SLAVE", 0);
		RIGHT_MOTOR = (int) Dashcomm.get("Constants/RIGHT_MOTOR", 0);
		RIGHT_SLAVE = (int) Dashcomm.get("Constants/RIGHT_SLAVE", 0);
	}
}