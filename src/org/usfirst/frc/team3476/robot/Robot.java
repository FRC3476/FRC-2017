package org.usfirst.frc.team3476.robot;

import org.usfirst.frc.team3476.utility.OrangeDrive;
import org.usfirst.frc.team3476.utility.OrangeDrivePIDWrapper;
import org.usfirst.frc.team3476.utility.PIDDashdataWrapper;

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
    OrangeDrive driveBase = new OrangeDrive(0,0,0,0);
    OrangeDrivePIDWrapper turn = new OrangeDrivePIDWrapper(driveBase, OrangeDrivePIDWrapper.Axis.TURN);
    OrangeDrivePIDWrapper move = new OrangeDrivePIDWrapper(driveBase, OrangeDrivePIDWrapper.Axis.MOVE);
    PIDDashdataWrapper angle = new PIDDashdataWrapper(""); //Need to add keypath
    PIDController pidTurn = new PIDController(0, 0, 0, angle, turn, .05); // add p, i, and d values, change refresh rate
	
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		chooser.addDefault("Default Auto", defaultAuto);
		chooser.addObject("My Auto", customAuto);
		SmartDashboard.putData("Auto choices", chooser);
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
		autoSelected = chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		System.out.println("Auto selected: " + autoSelected);
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		switch (autoSelected) {
		case customAuto:
			// Put custom auto code here
			break;
		case defaultAuto:
		default:
			// Put default auto code here
			break;
		}
	}

	/**
	 * This function is called periodically during operator control
	 */
	boolean peg = false;
	@Override
	public void teleopPeriodic() {
		double moveVal = joy.getRawAxis(1);
    	double turnVal = joy.getRawAxis(4);
    	
    	if (joy.getRawButton(1))
    	{
    		pidTurn.enable();
    		pidTurn.setSetpoint(0);
    		driveBase.setMove(0);
    	}
    	else
    	{    	
    		pidTurn.disable();
    		driveBase.arcadeDrive(moveVal, turnVal);
    	}
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
}
