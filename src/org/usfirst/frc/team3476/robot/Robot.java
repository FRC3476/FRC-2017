package org.usfirst.frc.team3476.robot;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.usfirst.frc.team3476.subsystem.Flywheels;
import org.usfirst.frc.team3476.subsystem.OrangeDrive;
import org.usfirst.frc.team3476.utility.Constants;
import org.usfirst.frc.team3476.utility.Dashcomm;
import org.usfirst.frc.team3476.utility.Toggle;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.CameraServer;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

	Joystick xbox = new Joystick(0);
	
	Toggle A = new Toggle();
	Toggle B = new Toggle();
	Toggle C = new Toggle();
	
	double speed = 2000;
	
	OrangeDrive orangeDrive;	
	Flywheels shooters;
	
	CANTalon feeder = new CANTalon(7);

	NetworkTable table = NetworkTable.getTable("SmartDashboard");
	
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
		orangeDrive = OrangeDrive.getInstance();
		shooters = Flywheels.getInstance();
		orangeDrive.addTask(mainExecutor);
	}
	
	private void startAutomaticCapture() {
		// TODO Auto-generated method stub
		UsbCamera usbCamera = new UsbCamera("USB Camera 0", 0);
		
        MjpegServer TurretCAM = new MjpegServer("serve_turretCam", 1181);
		TurretCAM.setSource(usbCamera); 
		
		MjpegServer GearCAM = new MjpegServer("serve_gearCam", 1182);
		GearCAM.setSource(usbCamera);
		
		MjpegServer DashCam = new MjpegServer("serve_DashCam", 1183);
		DashCam.setSource(usbCamera);
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

		first = true;

		orangeDrive.setRunningState(true);
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		if (first) {
			try {
				engine.eval(helperCode);
				engine.eval(code);
			} catch (ScriptException e) {
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
		
		feeder.changeControlMode(TalonControlMode.PercentVbus);
		
		A.input(xbox.getRawButton(1));
		B.input(xbox.getRawButton(2));
		C.input(xbox.getRawButton(3));

		if (B.rising()) {
			speed += 50;
		//	tbhController.setSetpoint(speed);
		}

		if (C.rising()) {
			speed -= 50;
		}
		if (xbox.getRawButton(1)) 
		{
			shooters.setLeftSetpoint(speed);
			feeder.set(-.5);
		}
		else
		{
			shooters.setLeftSetpoint(0);
			feeder.set(0);
		}
		
		table.putNumber("rpms", shooters.getLeftSpeed());
		table.putNumber("setpoint", speed);	
		NetworkTable.flush();

		//System.out.println("Setpoint:" + speed);
		//System.out.println("Actual:" + shooters.getLeftSpeed());
		
		double moveVal = xbox.getRawAxis(1);
		double turnVal = xbox.getRawAxis(4);
		// joystick pushed up gives -1 and down gives 1
		// it is also switch for turning
		orangeDrive.setManualDrive(-moveVal, -turnVal);
		
		shooters.setLeftSetpoint(speed);
		
		if(xbox.getRawButton(1)) {
			shooters.leftEnable();
		} else {
			shooters.leftDisable();
		}

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
