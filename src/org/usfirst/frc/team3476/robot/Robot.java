package org.usfirst.frc.team3476.robot;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.usfirst.frc.team3476.subsystem.Flywheel;
import org.usfirst.frc.team3476.subsystem.Gear;
import org.usfirst.frc.team3476.subsystem.Gear.GearState;
import org.usfirst.frc.team3476.subsystem.Hopper.HopperState;
import org.usfirst.frc.team3476.subsystem.Intake;
import org.usfirst.frc.team3476.subsystem.Intake.IntakeState;
import org.usfirst.frc.team3476.subsystem.OrangeDrive;
import org.usfirst.frc.team3476.subsystem.OrangeDrive.GearDrivingState;
import org.usfirst.frc.team3476.subsystem.OrangeDrive.ShiftState;
import org.usfirst.frc.team3476.subsystem.RobotTracker;
import org.usfirst.frc.team3476.subsystem.Shooter;
import org.usfirst.frc.team3476.subsystem.Shooter.ShooterState;
import org.usfirst.frc.team3476.subsystem.Turret;
import org.usfirst.frc.team3476.subsystem.VisionServer;
import org.usfirst.frc.team3476.utility.Constants;
import org.usfirst.frc.team3476.utility.Controller;
import org.usfirst.frc.team3476.utility.Dashcomm;
import org.usfirst.frc.team3476.utility.Path;
import org.usfirst.frc.team3476.utility.Path.Waypoint;
import org.usfirst.frc.team3476.utility.RigidTransform;
import org.usfirst.frc.team3476.utility.Rotation;
import org.usfirst.frc.team3476.utility.Translation;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;
import com.ctre.CANTalon.VelocityMeasurementPeriod;

import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.hal.PDPJNI;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.networktables.NetworkTablesJNI;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */

public class Robot extends IterativeRobot {

	Controller xbox;
	Controller joystick;
	Controller buttonBox;

	RobotTracker robotState;
	OrangeDrive orangeDrive;
	Shooter shooter;
	Intake intake;
	Gear gearMech;
	CANTalon climber;
	CANTalon climberSlave;
	CANTalon tempIntake = new CANTalon(Constants.IntakeId);
	
	boolean lowExposure = true;
	
	PowerDistributionPanel pdp = new PowerDistributionPanel(1);
	Future<?> logger;
	
	ScheduledExecutorService mainExecutor = Executors.newScheduledThreadPool(2);
	VisionServer vision = new VisionServer();

	
	ScriptEngineManager manager;
	ScriptEngine engine;	  	
	String code;
	String helperCode;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		NetworkTable.globalDeleteAll();					
		
		DigitalOutput turnOnJetson = new DigitalOutput(0);
		turnOnJetson.set(false);
		double initialTime = System.currentTimeMillis();
		while((System.currentTimeMillis() - initialTime) < 1000){
			// do nothing
		}
		turnOnJetson.set(true);
		new DigitalOutput(4).set(true);
		
		//Controllers
		xbox = new Controller(0);
		joystick = new Controller(1);
		buttonBox = new Controller(2);
		
		//Subsystems
		robotState = RobotTracker.getInstance();
		orangeDrive = OrangeDrive.getInstance();
		shooter = Shooter.getInstance();
		intake = Intake.getInstance();
		gearMech = Gear.getInstance();
		
		climber = new CANTalon(Constants.ClimberId);
		climber.changeControlMode(TalonControlMode.PercentVbus);
		climberSlave = new CANTalon(Constants.Climber2Id);
		climberSlave.changeControlMode(TalonControlMode.Follower);
		climberSlave.set(climber.getDeviceID());
		
		vision.schedule(mainExecutor);
		robotState.schedule(mainExecutor);
		orangeDrive.schedule(mainExecutor);
		shooter.schedule(mainExecutor);
		gearMech.schedule(mainExecutor);
		
		UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
		camera.setResolution(320, 240);
		
		manager = new ScriptEngineManager();
		engine = manager.getEngineByName("js");		
		
		// Put all variables for auto here
		engine.put("orangeDrive", orangeDrive);
		engine.put("DriverStation", DriverStation.getInstance());	
		Dashcomm.put("isJetsonOn", false);
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
		//double start = System.currentTimeMillis();
		orangeDrive.zeroSensors();
		robotState.setRunningState(true);
		orangeDrive.setRunningState(true);
		gearMech.setRunningState(true);
		shooter.setRunningState(true);
		if(!shooter.isHomed()){		
			shooter.setHome();
		}
		
		try {
			engine.eval("mainRunner.start()");
			
		} catch (ScriptException e) {
			System.out.println(e);
		}
		
		/*
		Path curve = new Path(new Waypoint(0,0, 50));
		curve.addWaypoint(new Waypoint(0, 120, 50));
		curve.addWaypoint(new Waypoint(20, 120, 50));
		orangeDrive.setAutoPath(curve, false);
		*/
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {

	}

	@Override
	public void disabledPeriodic(){		
		code = Dashcomm.get("Code", "");
		helperCode = Dashcomm.get("HelperCode", "");
		if(engine == null){
			System.out.println("null");
		}
		try
		{
			engine.eval(helperCode);
			engine.eval(code);
		}
		catch (ScriptException e)
		{
			e.printStackTrace();
		}		
	}
	
	
	@Override
	public void teleopInit() {
		robotState.setRunningState(false);
		orangeDrive.setRunningState(false);
		shooter.setRunningState(false);
		gearMech.setRunningState(false);
		vision.setRunningState(true);
	
		intake.setState(IntakeState.DOWN);
		
		if(!shooter.isHomed()){
			shooter.setHome();
		}
	}

	/**
	 * This function is called periodically during operator control
	 */
	
	boolean oldAxis = false;
	
	// 50 hz (20 ms)
	@Override
	public void teleopPeriodic() {
		xbox.update();
		joystick.update();
		buttonBox.update();
		double moveVal = xbox.getRawAxis(1);
		double rotateVal = -xbox.getRawAxis(4);
		
		if (gearMech.getWheelCurent() > 9.0)
		{
			xbox.setRumble(RumbleType.kRightRumble, 1);
		}
		else
			xbox.setRumble(RumbleType.kRightRumble, 0);
		
		if (xbox.getRawButton(1) || buttonBox.getRawButton(8)){
			orangeDrive.setManualGearPath();
		} else if (xbox.getFallingEdge(1) || joystick.getFallingEdge(12)){
			if(orangeDrive.getGearState() != GearDrivingState.DONE){
				gearMech.setState(GearState.PEG);
			}
		} else {
			orangeDrive.arcadeDrive(moveVal, rotateVal);
		}
		
		if (joystick.getRawButton(3)){
			gearMech.setSucking(.5);
		}
		else if (joystick.getRawButton(4)){
			gearMech.setSucking(-.25);
		}
		else {
			gearMech.setSucking(0);
		}
		
		if (joystick.getRawButton(2) || buttonBox.getRawButton(4)) {
			intake.setSucking(-0.8);
		} else if (buttonBox.getRawButton(3)){
			intake.setSucking(0.8);
		} else {
			intake.setSucking(0);
		}
		
		if (buttonBox.getRisingEdge(6) )
		{
			gearMech.setState(GearState.DOWN);
		}
		else if (buttonBox.getRisingEdge(7))
		{
			gearMech.setState(GearState.PEG);
		} else if(joystick.getRisingEdge(5)){
			gearMech.homeActuator();
		}
		
		if(xbox.getRawAxis(3) > .8){
			orangeDrive.setShiftState(ShiftState.MANUAL);
			orangeDrive.shiftDown();
		} else {
			orangeDrive.setShiftState(ShiftState.MANUAL);
			orangeDrive.shiftUp();			
		}
		//System.out.println("Current: " + gearMech.getCurrent());
		//System.out.println("Voltage: " + gearMech.getVoltage());
		
		if (buttonBox.getRawButton(5)){
			climber.set(.85);
			System.out.println("Climbing");
		} else if (joystick.getRawButton(7)) {
			climber.set(.425);
			System.out.println("Climbing");
		}
		else {
			climber.set(0);
		}
		
		if(joystick.getRawButton(1)){
			shooter.setState(ShooterState.SHOOT);
		} else{
			shooter.setState(ShooterState.IDLE);	
		}	
		
		if(buttonBox.getRawButton(1)){
			shooter.setTurretPower(0.2);
		} else if (buttonBox.getRawButton(2)){
			shooter.setTurretPower(-0.2);
		} else {
			shooter.setTurretPower(0);
		}
		
		oldAxis = xbox.getRawAxis(3) > .8;
		
		if((xbox.getRawButton(8) && xbox.getRisingEdge(7)) || (xbox.getRawButton(7) && xbox.getRisingEdge(8))){
			orangeDrive.toggleSimpleDrive();
		}		
	}

	@Override
	public void disabledInit() {
		orangeDrive.resetState();
		shooter.resetState();
		robotState.setRunningState(false);
		orangeDrive.setRunningState(false);		
		shooter.setRunningState(false);
		gearMech.setRunningState(false);
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {

	}
}
