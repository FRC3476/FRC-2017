package org.usfirst.frc.team3476.robot;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.usfirst.frc.team3476.subsystem.Flywheel;
import org.usfirst.frc.team3476.subsystem.OrangeDrive;
import org.usfirst.frc.team3476.utility.Constants;
import org.usfirst.frc.team3476.utility.Controller;
import org.usfirst.frc.team3476.utility.Dashcomm;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

	Controller xbox = new Controller(0);

	double speed = 2000;
	
	OrangeDrive orangeDrive;	
	Flywheel shooters;
	
	CANTalon feeder = new CANTalon(7);
	CANTalon intake = new CANTalon(8);
	CANTalon intake2 = new CANTalon(9);
	
	DigitalInput test = new DigitalInput(22);
	DigitalInput test2 = new DigitalInput(23);
	NetworkTable table = NetworkTable.getTable("");
	NetworkTable graph = NetworkTable.getTable("SmartDashboard");
	
	ScriptEngineManager manager;
	ScriptEngine engine;
	String code;
	String helperCode;

	Future<?> logger;
	// TODO: Determine best number of threads
	ScheduledExecutorService mainExecutor = Executors.newScheduledThreadPool(2);

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		Constants.updateConstants();
		shooters = new Flywheel(10, 11, 22);
		orangeDrive = OrangeDrive.getInstance();
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

		// Put all variables for auto here
		engine.put("orangeDrive", orangeDrive);

		// make function to set all running states
		orangeDrive.setRunningState(true);
		
		try {
			engine.eval(helperCode);
			engine.eval(code);
		} catch (ScriptException e) {
			System.out.println(e);
		}
		
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		
	}

	@Override
	public void teleopInit() {
		orangeDrive.setRunningState(true);
		shooters.setRunningState(true);
		logger = mainExecutor.scheduleAtFixedRate(new Runnable(){
			@Override
			public void run(){
				table.putNumber("rpms", shooters.getSpeed());
				table.putNumber("current", shooters.getCurrent());
				if(test.get()){
					table.putNumber("enter", 1);
				} else {
					table.putNumber("enter", 0);
				}

				if(test2.get()){
					table.putNumber("exit", 1);
				} else {
					table.putNumber("exit", 0);
				}
				
				graph.putNumber("rpms", shooters.getSpeed());
				graph.putNumber("setpoint", speed);
				NetworkTable.flush();
				
			}
		}, 0, 5, TimeUnit.MILLISECONDS);
	}

	/**
	 * This function is called periodically during operator control
	 */

	// 50 hz (20 ms)
	@Override
	public void teleopPeriodic() {
		xbox.update();
		feeder.changeControlMode(TalonControlMode.PercentVbus);

		if (xbox.getRisingEdge(2)) {
			speed += 50;
		}

		if (xbox.getRisingEdge(3)) {
			speed -= 50;
		}
		if (xbox.getRawButton(1)) 
		{
			shooters.setSetpoint(speed);
			feeder.set(-.5);
		}
		else
		{
			shooters.setSetpoint(0);
			feeder.set(0);
		}
		
		double moveVal = xbox.getRawAxis(1);
		double turnVal = xbox.getRawAxis(4);
		// joystick pushed up gives -1 and down gives 1
		// it is also switch for turning
		//orangeDrive.setManualDrive(-moveVal, -turnVal);
		if(xbox.getRawButton(1)) {
			shooters.enable();
		} else {
			shooters.disable();
		}

		if(xbox.getRawButton(4)){
			intake.set(0.5);
			intake2.set(0.5);
		} else {
			intake.set(0);
			intake2.set(0);
		}	
		
	}

	@Override
	public void disabledInit() {
		orangeDrive.setRunningState(false);
		if(logger != null){
			logger.cancel(true);		
		}
		shooters.setRunningState(false);
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {

	}
}
