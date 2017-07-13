package org.usfirst.frc.team3476.subsystem;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.usfirst.frc.team3476.utility.Constants;
import org.usfirst.frc.team3476.utility.Dashcomm;
import org.usfirst.frc.team3476.utility.Interpolable;
import org.usfirst.frc.team3476.utility.Rotation;
import org.usfirst.frc.team3476.utility.Threaded;
import org.usfirst.frc.team3476.utility.Translation;

import edu.wpi.first.wpilibj.DriverStation;

public class VisionTracking extends Threaded {

	private ExecutorService workers;
	private DatagramSocket listener;
	
	private Rotation gearAngle;
	private Rotation turretAngle;
	private Interpolable lookupTable;
	private double desiredFlywheelSpeed;
	
	public VisionTracking() {
		super(10);
		try {
			listener = new DatagramSocket(5800);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		workers = Executors.newFixedThreadPool(2);
		lookupTable = new Interpolable();
		lookupTable.addNumber(10.0, 10.0);		
	}

	@Override
	public void update() {
		byte[] buffer = new byte[2048];
		System.out.println("pritning");
		DriverStation.getInstance().reportError("printing", false);
		DatagramPacket msg = new DatagramPacket(buffer, buffer.length);
		try {
			listener.receive(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
		workers.execute(new MessageHandler(msg));		
	}
	
	public double getBoilerDistance(){
		double x = 1;
		double y = Dashcomm.get("boilerX", 0);
		double z = Dashcomm.get("boilerY", 0);		
		double distance = Constants.BoilerHeight / Math.tan(Math.toRadians(z / 1280 * Constants.yCameraFOV + 62)); //to radians first
		return distance;		
		/*
		x is forwards from camera
		y is to the left from camera
		z is up from the camera
		x = x * yawOffset.cos() - y * yawOffset.sin();
		y = x * yawOffset.cos() + y * yawOffset.sin();
		
		x = x * pitchOffset.cos() - z * pitchOffset.sin();
		z = x * pitchOffset.cos() + z * pitchOffset.sin();
		
		distance = Constants.BoilerHeight / z * Math.hypot(x, y);			
		*/
	}	
	
	public synchronized double getFlywheelSpeed(){
		return desiredFlywheelSpeed;
	}
	
	class MessageHandler extends Threaded {
		
		DatagramPacket packet;
		
		public MessageHandler(DatagramPacket packet){
			this.packet = packet;
		}
		
		public void update(){
			String rawMessage = new String(packet.getData(), 0, packet.getLength());

			DriverStation.getInstance().reportError(rawMessage, false);
			
		}
	}
}
