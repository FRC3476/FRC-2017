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
import org.usfirst.frc.team3476.subsystem.GearMech;
import org.usfirst.frc.team3476.subsystem.GearMech.GearState;
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
import org.usfirst.frc.team3476.utility.RigidTransform;
import org.usfirst.frc.team3476.utility.Rotation;
import org.usfirst.frc.team3476.utility.Translation;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;
import com.ctre.CANTalon.VelocityMeasurementPeriod;

import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID.Hand;
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
	Controller joystick = new Controller(1);

	double speed = 3000;

	RobotTracker robotState;
	OrangeDrive orangeDrive;
	Gear gear;
	GearMech gearMech;
	Intake intake;
	CANTalon feeder = new CANTalon(Constants.IntakeFeederId);
	CANTalon star = new CANTalon(Constants.StarFeederId);
	
//	Turret leftTurret;
//	Turret rightTurret;
	CANTalon climber, climber2;
	double highestCurrent = 0;

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
	DigitalOutput led =  new DigitalOutput(4);
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
		led.set(true);
		//Subsystems
		/*
		shooter1 = new Flywheel(Constants.LeftMasterFlywheelId, Constants.LeftSlaveFlywheelId, 22);
		shooter2 = new Flywheel(Constants.RightMasterFlywheelId, Constants.RightSlaveFlywheelId, 23);
		*/
		robotState = RobotTracker.getInstance();
		orangeDrive = OrangeDrive.getInstance();
		gear = Gear.getInstance();
		intake = Intake.getInstance();
		gearMech = GearMech.getInstance();
//		leftTurret = new Turret(Constants.LeftTurretId);
//		rightTurret = new Turret(Constants.RightTurretId);
		climber = new CANTalon(Constants.ClimberId);
		climber.changeControlMode(TalonControlMode.PercentVbus);
		climber2 = new CANTalon(Constants.Climber2Id);
		climber2.changeControlMode(TalonControlMode.PercentVbus);

		robotState.addTask(mainExecutor);
		orangeDrive.addTask(mainExecutor);
		gear.addTask(mainExecutor);

		
		manager = new ScriptEngineManager();
		engine = manager.getEngineByName("js");
		
		// Put all variables for auto here
		engine.put("orangeDrive", orangeDrive);
		engine.put("DriverStation", DriverStation.getInstance());
		
		//if the pulse doesn't work
		/*
		gearCamera = new UsbCamera("gearCam", 0);
		
		boilerCamera = new UsbCamera("boilerCam", 1);
		driverCamera = new UsbCamera("driverCam", 2);
		
		//Main streamer is used to switch between camera streams
		mainStreamer = new MjpegServer("gearStream", 1180);
		mainStreamer.setSource(gearCamera);
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
		//double start = System.currentTimeMillis();
		robotState.setRunningState(true);
		orangeDrive.setRunningState(true);
		gear.setRunningState(true);
		orangeDrive.setInverse(true);
		
		try {
			engine.eval(code);
			
		} catch (ScriptException e) {
			System.out.println(e);
		}
	
		// inversed
		/*
		
		/*
		Path drivingPath = new Path(new Waypoint(0, 30, 30));
		//drivingPath.addWaypoint(new Waypoint(0, 30, 0));
		orangeDrive.setAutoPath(drivingPath, true);
		while(!orangeDrive.isDone()){
			
		}
		
		double start = System.currentTimeMillis();
		while(System.currentTimeMillis() - start < 750){
			if(DriverStation.getInstance().isOperatorControl()){
				break;
			}
		}
		

		//System.out.println("setting rotate");
		
		//orangeDrive.setRotation(Rotation.fromDegrees(30));
		
		orangeDrive.setGearPath();
		while(!orangeDrive.isDone()){
			if(DriverStation.getInstance().isOperatorControl()){
				break;
			}
		}
		orangeDrive.setManualDrive(0, 0);		
		
		orangeDrive.setAutoPath(new Path(new Waypoint(0, 83, 20)), true);
		while(!orangeDrive.isDone()){
			if(DriverStation.getInstance().isOperatorControl()){
				break;
			}
		}
		
		orangeDrive.setRotation(Rotation.fromDegrees(130));
		while(!orangeDrive.isDone()){
			if(DriverStation.getInstance().isOperatorControl()){
				break;
			}
		}
		
		double start = System.currentTimeMillis();
		while(System.currentTimeMillis() - start < 1000){
			if(DriverStation.getInstance().isOperatorControl()){
				break;
			}
		}
		
		orangeDrive.setGearPath();

		while(!orangeDrive.isDone()){
			if(DriverStation.getInstance().isOperatorControl()){
				break;
			}
		}
		
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
		}
		catch (ScriptException e)
		{
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void teleopInit() {
		robotState.setRunningState(true);
		orangeDrive.setRunningState(true);
		gear.setRunningState(true);
		//System.out.println("created runnable");
		/*
		mainExecutor.scheduleAtFixedRate(new Runnable(){
			@Override
			public void run(){
				for(int i = 0; i < 16; i++){
					NetworkTable.getTable("").putNumber("channel" + i, pdp.getCurrent(i));
				}				
			}
		}, 0, 50, TimeUnit.MILLISECONDS);
		*/
	}

	/**
	 * This function is called periodically during operator control
	 */
	boolean homed = false;
	// 50 hz (20 ms)
	@Override
	public void teleopPeriodic() {
		xbox.update();
		joystick.update();
		double moveVal = -xbox.getRawAxis(1);
		double turnVal = -xbox.getRawAxis(4);		
				
//		if (xbox.getRawButton(5))
//		{
//			orangeDrive.setNormal();
//		}
//		if (xbox.getRawButton(6))
//		{
//			orangeDrive.setInvert();
//		}
//
//		if(xbox.getRawButton(1)){
//			orangeDrive.setGearPath();
//		} else {
//			orangeDrive.setManualDrive(moveVal, turnVal);
//			orangeDrive.setBrake(xbox.getRawButton(3));
//		}
//		
//		if(xbox.getRawAxis(2) > 0.8){
//			orangeDrive.shiftUp();
//		}
//
//		if(xbox.getRawAxis(3) > 0.8){
//			orangeDrive.shiftDown();
//		}
//		
//		//ACTUAL STUFF
//		if(joystick.getPOV(0) == 180 ||  joystick.getPOV(0) == 225 || joystick.getPOV(0) == 135){
//			intake.setSucking(0.5);
//		} else if(joystick.getPOV(0) == 315 || joystick.getPOV(0) == 0 || joystick.getPOV(0) == 45){
//			intake.setSucking(-0.5);
//		} else {
//			intake.setSucking(0);
//		}
		//Intake Dropdown
//		if (joystick.getRawButton(5))
//		{
//			intake.setState(IntakeState.UP);
//		}
//		
//		if (joystick.getRawButton(3))
//		{
//			intake.setState(IntakeState.DOWN);
//		}
		//GearMech DropDown
		/*
		if(joystick.getRawButton(7))
		{
			gearMech.moveDropDown(GearState.UP);
		}
		else if(joystick.getRawButton(13))
		{
			gearMech.moveDropDown(GearState.DOWN);//Do we always want to intake when the GearMech is down?
		}*/
		
		
		//orangeDrive.setManualDrive(moveVal, turnVal);
		
		if (xbox.getRisingEdge(1)) //replace with button
		{
			gearMech.manualPegInsert();
		}

		/*
		if (xbox.getRawButton(3))
		{
			gearMech.changeControlMode(TalonControlMode.PercentVbus);
			gearMech.setActuator(.2);
		}
		else if (xbox.getRawButton(4))
		{
			gearMech.changeControlMode(TalonControlMode.PercentVbus);
			gearMech.setActuator(-.2);
		}
		else
		{
			gearMech.changeControlMode(TalonControlMode.PercentVbus);
			gearMech.setActuator(0);
		}*/
		
		if (xbox.getRawButton(2))
		{
			if (!homed)
			{
				gearMech.setActuator(.2);
				if (gearMech.getCurrent() > 1.5)
				{
					gearMech.resetPosition();
					gearMech.setActuator(0);
					System.out.println("HOMED");
					homed = true;
				}
			}
			else
			{
				gearMech.setActuator(0);
			}
		}
		
		if (xbox.getRawButton(3))
			gearMech.setActuator(.2);
		else if (xbox.getRawButton(4))
			gearMech.setActuator(-.2);
		
		if (xbox.getFallingEdge(3))
			gearMech.setActuator(0);
		if (xbox.getFallingEdge(4))
			gearMech.setActuator(0);
			
		
		if (xbox.getRisingEdge(7)){
			//gearMech.changeControlMode(TalonControlMode.Position);
			gearMech.setActuatorPosition(GearMech.DOWN);
			System.out.println("Down " + GearMech.DOWN);
		}
		if (xbox.getRisingEdge(8)){
			//gearMech.changeControlMode(TalonControlMode.Position);
			gearMech.setActuatorPosition(GearMech.UP);
			System.out.println("Up " + GearMech.UP);
		}
		if (xbox.getRisingEdge(9))
		{
			gearMech.setActuatorPosition(GearMech.PEG);
		}
		if (xbox.getRisingEdge(10))
		{
			gearMech.setActuatorPosition(GearMech.HOME);
		}
		
		System.out.println("Current " + gearMech.getCurrent());
		System.out.println("Voltage " + gearMech.getVoltage());
		if (xbox.getRawButton(5))
		{
			gearMech.setSucking(.5);
		}
		else if (xbox.getRawButton(6))
		{
			gearMech.setSucking(-.5);
		}
		else
		{
			gearMech.setSucking(0);
		}
		
		if(xbox.getRisingEdge(-1))
		{
			System.out.println("Position" + gearMech.getPosition());
		}
		/*
		if (joystick.getRawButton(7)) {
			shooter1.setPercent(0.5);
			shooter2.setPercent(0.5);
		} else {
			shooter1.setPercent(0);
			shooter2.setPercent(0);
			star.set(0);
		}
		 */
//		if(joystick.getRawButton(9)){
//			gear.setGearMech(true);
//			gear.setRunningState(false);
//		} else {			
//			gear.setRunningState(true);
//		}
//
//		if(joystick.getRisingEdge(8)){
//			gear.toggleFlap();
//		}
//	
//		if(joystick.getRawButton(10)){
//			star.set(0.95);
//		} else {  
//			star.set(0);
//		}
//		
		/*
		if (xbox.getRawButton(2)){
			climber.set(1);
			climber2.set(1);
		} else {
			climber.set(0);
			climber2.set(0);
		}
		*/
	}

	@Override
	public void disabledInit() {
		robotState.setRunningState(false);
		orangeDrive.setRunningState(false);		
		if (logger != null) {
			logger.cancel(true);
		}
		gear.setRunningState(false);
		
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
