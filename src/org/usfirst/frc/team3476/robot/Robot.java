package org.usfirst.frc.team3476.robot;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.usfirst.frc.team3476.subsystem.Drive;
import org.usfirst.frc.team3476.utility.OrangeDrive;
import org.usfirst.frc.team3476.utility.OrangeDrivePIDWrapper;
import org.usfirst.frc.team3476.utility.PIDDashdataWrapper;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

	Joystick joy = new Joystick(0);
	CANTalon DriveRight1 = new CANTalon(4);
	CANTalon DriveRight2 = new CANTalon(5);
	CANTalon DriveLeft1 = new CANTalon(7);
	CANTalon DriveLeft2 = new CANTalon(8);
	
	Drive orangeDrive = new Drive(DriveLeft1, DriveLeft2, DriveRight1, DriveRight2);
	
	ScheduledExecutorService mainExecutor = Executors.newScheduledThreadPool(1);
	
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {

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

	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {

	}
	

	
	public void teleopInit() {
		orangeDrive.startTask(mainExecutor);
		orangeDrive.createPIDController(0, 0, 0, "angle", .05);
	}
	
	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		double moveVal = joy.getRawAxis(1);
    	double turnVal = joy.getRawAxis(4);
    	orangeDrive.updateDriveValues(moveVal, turnVal);
    	
    	if (joy.getRawButton(1))
    		orangeDrive.setState(Drive.DriveState.PEG);
    	else
    	{
    		orangeDrive.setState(Drive.DriveState.MANUAL);
    		System.out.println("set state");
    	}
	}
	
	public void disabledInit() {
		orangeDrive.endTask();
	}
	
	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
}
