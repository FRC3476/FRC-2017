package org.usfirst.frc.team3476.utility;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public final class Constants
{	
	private Constants(){}
	
	
	private static Set<String> keys;
	private static NetworkTable ntable = NetworkTable.getTable("/Constants/");
	private static Map<String, String> table = new HashMap<String, String>();
	
	//Key values
	private static String minInputKey = "MINIMUN_INPUT";
	private static String maxInputKey = "MAXIMUM_INPUT";
	private static String minOutputKey = "MINIMUM_OUTPUT";
	private static String maxOutputKey = "MAXIMUM_OUTPUT";
	private static String wheelDiameterKey = "WHEEL_DIAMETER";
	private static String leftMasterMotorKey = "LEFT_MOTOR";
	private static String leftSlaveMotorKey = "LEFT_SLAVE";
	private static String rightMasterMotorKey = "RIGHT_MOTOR";
	private static String rightSlaveMotorKey = "RIGHT_SLAVE";
	
	//Constants
	//Make these public
	
	public static double MINIMUM_INPUT = OrangeUtility.cleanDoubleParse(table.get(minInputKey));
	public static double MAXIMUM_INPUT = OrangeUtility.cleanDoubleParse(table.get(maxInputKey));
	public static double MINIMUM_OUTPUT = OrangeUtility.cleanDoubleParse(table.get(minOutputKey));
	public static double MAXIMUM_OUTPUT = OrangeUtility.cleanDoubleParse(table.get(maxOutputKey));
	public static double WHEEL_DIAMETER = OrangeUtility.cleanDoubleParse(table.get(wheelDiameterKey));
	
	public static int LEFT_MOTOR = OrangeUtility.cleanIntParse(table.get(leftMasterMotorKey));
	public static int LEFT_SLAVE = OrangeUtility.cleanIntParse(table.get(leftSlaveMotorKey));
	public static int RIGHT_MOTOR = OrangeUtility.cleanIntParse(table.get(rightMasterMotorKey));
	public static int RIGHT_SLAVE = OrangeUtility.cleanIntParse(table.get(rightSlaveMotorKey));
	
	public static void updateConstants()
	{
		keys = ntable.getKeys();
		for (String key : keys)
		{
			key = key.trim();
			table.put(key, ntable.getString(key,"").trim());
		}
	}
}