package org.usfirst.frc.team3476.robot;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.usfirst.frc.team3476.subsystem.OrangeDrive;

import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.UsbCamera;
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

	Joystick joy = new Joystick(0);
	OrangeDrive orangeDrive = OrangeDrive.getInstance();

	// TODO: Camera will go on the jetson so idk :|
	UsbCamera cam = new UsbCamera("camera", 0);
	MjpegServer server = new MjpegServer("camServer", 8080);

	// TODO: Determine best number of threads
	ScheduledExecutorService mainExecutor = Executors.newScheduledThreadPool(2);

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		orangeDrive.addTask(mainExecutor);
		server.setSource(cam);
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
	}

	/**
	 * This function is called periodically during operator control
	 */

	// 50 hz (20 ms)
	@Override
	public void teleopPeriodic() {
		double moveVal = joy.getRawAxis(1);
		double turnVal = joy.getRawAxis(4);
		// TODO: Use Toggle to get only rising edge
		if (joy.getRawButton(1)) {
			orangeDrive.setGearPath();
		} else {
			orangeDrive.setManualDrive(moveVal, turnVal);
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
