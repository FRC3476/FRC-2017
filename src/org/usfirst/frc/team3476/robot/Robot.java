package org.usfirst.frc.team3476.robot;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.usfirst.frc.team3476.subsystem.OrangeDrive;
import org.usfirst.frc.team3476.utility.Constants;
import org.usfirst.frc.team3476.utility.Dashcomm;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

	Joystick xbox = new Joystick(0);
	OrangeDrive orangeDrive = OrangeDrive.getInstance();
	
	
	ScriptEngineManager manager;
	ScriptEngine engine;
	String code;
	String helperCode;
	boolean first;

	// TODO: Determine best number of threads
	ScheduledExecutorService mainExecutor = Executors.newScheduledThreadPool(2);
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		Constants.updateConstants();
		orangeDrive.addTask(mainExecutor);		
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
		orangeDrive.setRunningState(true);
		manager = new ScriptEngineManager();
		engine = manager.getEngineByName("js");
		code = Dashcomm.get("Code", "");
		helperCode = Dashcomm.get("HelperCode", "");
		
		//Put all variables for auto here
		engine.put("orangeDrive", orangeDrive);
		
		first = true;

		orangeDrive.setRunningState(true);
	}
	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		if (first)
		{
			try
			{
				engine.eval(helperCode);
				engine.eval(code);
			}
			catch (ScriptException e)
			{
				System.out.println(e);
			}
			
			first = false;
		}
	}

	@Override
	public void teleopInit() {
		orangeDrive.setRunningState(true);
	}

	/**
	 * This function is called periodically during operator control
	 */

	// 50 hz (20 ms)
	@Override
	public void teleopPeriodic() {
		double moveVal = xbox.getRawAxis(1);
		double turnVal = xbox.getRawAxis(4);
		// joystick pushed up gives -1 and down gives 1
		// it is also switch for turning
		orangeDrive.setManualDrive(-moveVal, -turnVal);
		
	}

	@Override
	public void disabledInit() {
		orangeDrive.setRunningState(false);
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {

	}
}
