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
import org.usfirst.frc.team3476.subsystem.Intake;
import org.usfirst.frc.team3476.subsystem.Intake.IntakeState;
import org.usfirst.frc.team3476.subsystem.OrangeDrive;
import org.usfirst.frc.team3476.subsystem.RobotTracker;
import org.usfirst.frc.team3476.subsystem.Turret;
import org.usfirst.frc.team3476.utility.Constants;
import org.usfirst.frc.team3476.utility.Controller;
import org.usfirst.frc.team3476.utility.Dashcomm;
import org.usfirst.frc.team3476.utility.Path;
import org.usfirst.frc.team3476.utility.Path.Waypoint;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;
import com.ctre.CANTalon.VelocityMeasurementPeriod;

import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.hal.PDPJNI;
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

	double speed = 3000;

	RobotTracker robotState;
	OrangeDrive orangeDrive;
	Flywheel shooters;
	Gear gear;
	//Intake intake;
	
	Turret leftTurret;
	Turret rightTurret;
	CANTalon climber;

	NetworkTable table = NetworkTable.getTable("");
	NetworkTable graph = NetworkTable.getTable("SmartDashboard");
	DigitalOutput turnOnJetson = new DigitalOutput(0);
	
	ScriptEngineManager manager;
	ScriptEngine engine;
	
	String code;
	String helperCode;
	/*
	MjpegServer mainStreamer;
	MjpegServer secondStreamer;
	MjpegServer thirdStreamer;
	
	UsbCamera gearCamera;
	UsbCamera boilerCamera;
	UsbCamera driverCamera;
	*/
	PWM led = new PWM(0);
	PowerDistributionPanel pdp = new PowerDistributionPanel(1);
	Future<?> logger;
	// TODO: Determine best number of threads
	ScheduledExecutorService mainExecutor = Executors.newScheduledThreadPool(2);

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		
		
		turnOnJetson.set(false);
		double initialTime = System.currentTimeMillis();
		while((System.currentTimeMillis() - initialTime) < 1000){
			// do nothing
		}
		turnOnJetson.set(true);
		
		
		//Subsystems
		//shooters = new Flywheel(10, 11, 22);
		robotState = RobotTracker.getInstance();
		orangeDrive = OrangeDrive.getInstance();
		gear = Gear.getInstance();
		//intake = Intake.getInstance();
		leftTurret = new Turret(Constants.LeftTurretId);
		rightTurret = new Turret(Constants.RightTurretId);
		climber = new CANTalon(Constants.ClimberId);
		climber.changeControlMode(TalonControlMode.PercentVbus);
		
		//if the pulse doesn't work
		
		/*
		gearCamera = new UsbCamera("gearCam", 0);
		boilerCamera = new UsbCamera("boilerCam", 1);
		driverCamera = new UsbCamera("driverCam", 2);
		
		//Main streamer is used to switch between camera streams
		mainStreamer = new MjpegServer("gearStream", 1180);
		*/
		
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
		robotState.addTask(mainExecutor);
		orangeDrive.addTask(mainExecutor);
		gear.addTask(mainExecutor);
		//shooters.addTask(mainExecutor);
		
		robotState.setRunningState(true);
		orangeDrive.setRunningState(true);
		
		/*manager = new ScriptEngineManager();
		engine = manager.getEngineByName("js");
		code = Dashcomm.get("Code", "");
		helperCode = Dashcomm.get("HelperCode", "");

		// Put all variables for auto here
		engine.put("orangeDrive", orangeDrive);
		//shooters.setRunningState(true);
		try {
			engine.eval(helperCode);
			engine.eval(code);
		} catch (ScriptException e) {
			System.out.println(e);
		}*/
		
		orangeDrive.setAutoPath(new Path(new Waypoint(0, 120, 10)));

	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {

	}

	@Override
	public void teleopInit() {
		robotState.addTask(mainExecutor);
		orangeDrive.addTask(mainExecutor);
		gear.addTask(mainExecutor);
		//shooters.addTask(mainExecutor);
		
		robotState.setRunningState(true);
		orangeDrive.setRunningState(true);
		gear.setRunningState(true);
		//shooters.setRunningState(true);
		logger = mainExecutor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
			}
		}, 0, 10, TimeUnit.MILLISECONDS);
		System.out.println("created runnable");
	}

	/**
	 * This function is called periodically during operator control
	 */

	// 50 hz (20 ms)
	@Override
	public void teleopPeriodic() {
		xbox.update();
		double moveVal = -xbox.getRawAxis(1);
		double turnVal = -xbox.getRawAxis(4);
		
		led.setRaw(255);
		if(xbox.getRawButton(1)){
			orangeDrive.setGearPath();
		} else {
			orangeDrive.setManualDrive(moveVal, turnVal);
			
		}
		//System.out.println(testSensor.get());
		//System.out.println(testSensor2.get());
		
		//ACTUAL STUFF
		/*

		if(xbox.getPOV(0) == 180){
			intake.setSucking(0.5);
		} else if(xbox.getPOV(0) == 0){
			intake.setSucking(-0.5);
		} else {
			intake.setSucking(0);
		}
				
		if (xbox.getRawButton(2))
		{
			intake.setState(IntakeState.DOWN);
		}
		if (xbox.getRawButton(3))
		{
			intake.setState(IntakeState.UP);
		}
		
		if(climber.getOutputCurrent() < 40){
			if (xbox.getRawButton(4))
				climber.set(-.85);
			else
				climber.set(0);
		}
		if(climber.getOutputCurrent() > 40){
			System.out.println(climber.getOutputCurrent());
		}
		/*
		System.out.println("channel 0 " + pdp.getCurrent(0));
		System.out.println("channel 3 " + pdp.getCurrent(3));
		*/
	
		if (xbox.getRawButton(5))
		{
			orangeDrive.shiftDown();
		}
		if (xbox.getRawButton(6))
		{
			orangeDrive.shiftUp();
		}
		
	//	intake.setFeeder(xbox.getRawButton(8));

		//intake.setFeeder(xbox.getRawButton(5));
		
	//	gear.setGearMech(xbox.getRawButton(7));
		
		double leftTrigger = -xbox.getRawAxis(2);
		double rightTrigger = xbox.getRawAxis(3);
		double triggers = -(leftTrigger + rightTrigger);

		table.putNumber("current", pdp.getCurrent(0));
		NetworkTable.flush();
		
		leftTurret.setManual(triggers);
		rightTurret.setManual(triggers);
		
		/*
		//END ACTUAL
		
		if (xbox.getRisingEdge(2)) {
			speed += 50;
		}

		if (xbox.getRisingEdge(3)) {
			speed -= 50;
		}

		if (xbox.getRawButton(1)) {
			shooters.setSetpoint(speed);
			shooters.enable();
			feeder.set(-.5);
		} else {
			shooters.setSetpoint(0);
			shooters.disable();
			feeder.set(0);
		}
		
		
		double moveVal = -xbox.getRawAxis(1);
		double turnVal = -xbox.getRawAxis(4);
		
		orangeDrive.setManualDrive(moveVal, turnVal);
		
		
		// test one variable at a time
		if (xbox.getRawButton(4)) {
			shooters.setVelocityMeasurementPeriod(VelocityMeasurementPeriod.Period_10Ms);
			shooters.setVelocityMeasurementWindow(64);
		} else {
			shooters.setVelocityMeasurementPeriod(VelocityMeasurementPeriod.Period_100Ms);
			shooters.setVelocityMeasurementWindow(64);
		}*/
	}

	@Override
	public void disabledInit() {
		robotState.setRunningState(false);
		orangeDrive.setRunningState(false);		
		if (logger != null) {
			logger.cancel(true);
		}
		gear.setRunningState(false);
		
		robotState.endTask();
		orangeDrive.endTask();
		gear.endTask();
		//shooters.endTask();
		
		//shooters.setRunningState(false);
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {

	}
}
