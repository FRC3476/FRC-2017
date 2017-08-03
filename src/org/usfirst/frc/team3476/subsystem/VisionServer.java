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
		System.out.println("listening");
		DatagramPacket msg = new DatagramPacket(buffer, buffer.length);
		try {
			listener.receive(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
		workers.execute(new MessageHandler(msg));
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
			double distance = Constants.BoilerHeight / Math.tan(Math.toRadians(z / 1280 * Constants.yCameraFOV + 62)); //to radians first
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
			if(((String) message.get("target")).equals("boiler")){
				turretToBoiler.add(new VisionData((double) message.get("angle"), distance, System.nanoTime() - (long) message.get("timestamp")));
			}	
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
