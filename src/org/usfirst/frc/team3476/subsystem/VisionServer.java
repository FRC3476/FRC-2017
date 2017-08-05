package org.usfirst.frc.team3476.subsystem;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.usfirst.frc.team3476.utility.CircularQueue;
import org.usfirst.frc.team3476.utility.Constants;
import org.usfirst.frc.team3476.utility.Dashcomm;
import org.usfirst.frc.team3476.utility.Threaded;
import org.json.simple.*;

public class VisionServer extends Threaded {

	private static final VisionServer instance = new VisionServer();
	private ExecutorService workers;
	private DatagramSocket listener;
	private CircularQueue<VisionData> turretToBoiler;
	
	
	public static VisionServer getInstance(){
		return instance;
	}
	
	private VisionServer() {
		turretToBoiler = new CircularQueue<VisionData>(10);
		try {
			listener = new DatagramSocket(5800);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		workers = Executors.newFixedThreadPool(2);
	}

	@Override
	public void update() {
		byte[] buffer = new byte[2048];
		DatagramPacket msg = new DatagramPacket(buffer, buffer.length);
		try {
			listener.receive(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
		workers.execute(new MessageHandler(msg));
	}
		
	public double GetBoilerAngle(double time){
		return turretToBoiler.get(0).angle;
	}
	
	private class MessageHandler extends Threaded {
		
		DatagramPacket packet;
		
		public MessageHandler(DatagramPacket packet){
			this.packet = packet;
		}
		
		public void update(){
			String rawMessage = new String(packet.getData(), 0, packet.getLength());
			JSONObject message = (JSONObject) JSONValue.parse(rawMessage);
			
			double x = 1;
			double y = (double) message.get("x");
			double z = (double) message.get("y");
			double distance = Constants.BoilerHeight / Math.tan(Math.toRadians(z / 720 * Constants.yCameraFOV + 62)); //to radians first
			double angle = y / 1280 * Constants.xCameraFOV;
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
			/*
			18.8 90
			23 130
			if(((String) message.get("target")).equals("boiler")){
				turretToBoiler.add(new VisionData((double) message.get("x"), distance, System.nanoTime() - (long) message.get("timestamp")));
			}
			*/
			turretToBoiler.add(new VisionData(angle, 0, 0));
		}
	}
	
	static private class VisionData {
		public double angle;
		public double distance;
		public long time;
		
		public VisionData(double angle, double distance, long time){
			this.angle = angle;
			this.distance = distance;
			this.time = time;
		}
	}

}
