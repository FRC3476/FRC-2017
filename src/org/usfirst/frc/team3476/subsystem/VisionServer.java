package org.usfirst.frc.team3476.subsystem;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.usfirst.frc.team3476.utility.Constants;
import org.usfirst.frc.team3476.utility.Rotation;
import org.usfirst.frc.team3476.utility.Threaded;

public class VisionServer extends Threaded {

	private class MessageHandler extends Threaded {

		DatagramPacket packet;

		public MessageHandler(DatagramPacket packet) {
			this.packet = packet;
		}

		@Override
		public void update() {
			String rawMessage = new String(packet.getData(), 0, packet.getLength());
			JSONObject message = (JSONObject) JSONValue.parse(rawMessage);
			double x = 1;
			double y = (double) message.get("x");
			double z = (double) message.get("y");

			double distance = Constants.BoilerHeight / Math.tan(Math.toRadians(z / 720 * Constants.yCameraFOV + 62)); // to
																														// radians
																														// first
			double angle = y / 1280 * Constants.xCameraFOV;

			double time = System.nanoTime() - (double) message.get("time") - TimeUnit.MILLISECONDS.toNanos(3);
			/*
			 * x is forwards from camera y is to the left from camera z is up
			 * from the camera x = x * yawOffset.cos() - y * yawOffset.sin(); y
			 * = x * yawOffset.cos() + y * yawOffset.sin();
			 * 
			 * x = x * pitchOffset.cos() - z * pitchOffset.sin(); z = x *
			 * pitchOffset.cos() + z * pitchOffset.sin();
			 */
			double distanceN = Constants.BoilerHeight / z * Math.hypot(x, y);
			double angleN = new Rotation(x, y).getDegrees();
			 
		
			synchronized (this) {
				boilerData.angle = angle;
				boilerData.distance = distance;
				boilerData.time = (long) time;				
			}
			// move to storing an x, y position value instead
		}
	}

	// TODO: Change to not implementing TimeStampedData and instead of
	// angle/distance to x,y coordinates
	static public class VisionData {
		private double angle;
		private double distance;
		private long time;

		public VisionData(double angle, double distance, long time) {
			this.angle = angle;
			this.distance = distance;
			this.time = time;
		}

		public double getAngle() {
			return angle;
		}

		public double getDistance() {
			return distance;
		}

		public long getTime() {
			return time;
		}
	}

	private static final VisionServer instance = new VisionServer();

	public static VisionServer getInstance() {
		return VisionServer.instance;
	}

	private ExecutorService workers;

	private DatagramSocket listener;

	private VisionData boilerData;

	private VisionServer() {
		boilerData = new VisionData(0, 0, 0);
		try {
			listener = new DatagramSocket(5800);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		workers = Executors.newFixedThreadPool(2);
	}

	synchronized public VisionData getBoilerData() {
		return boilerData;
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

}
