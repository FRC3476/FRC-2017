package org.usfirst.frc.team3476.utility;

import java.util.ArrayList;
import java.util.List;


public class Path {

	private List<Waypoint> pathPoints;
	private double currentPathSpeed;
	// TODO: Remake algo to match new intended behavior
	public Path(Waypoint initialPoint) {
		pathPoints =  new ArrayList<Waypoint>();
		pathPoints.add(initialPoint);
	}

	public void addWaypoint(Waypoint nextPoint) {
		pathPoints.add(nextPoint);
	}

	public Translation getLookAheadPoint(Translation robotPose, double lookAheadDistance) {
		Waypoint prevPoint = null;
		for (Waypoint pathPoint : pathPoints) {
			if (lookAheadDistance <= pathPoint.position.getDistanceTo(robotPose)) {
				if (prevPoint == null) {
					currentPathSpeed = pathPoint.getSpeed();
					return pathPoint.getPosition();
				}
				// TODO: fix this logic http://mathworld.wolfram.com/Circle-LineIntersection.html
				double x1 = prevPoint.getPosition().getX();
				double x2 = pathPoint.getPosition().getX();
				double y1 = prevPoint.getPosition().getY();
				double y2 = pathPoint.getPosition().getY();
				double dX = x2 - x1;
				double dY = y2 - y1;
				double dR2 = dX * dX + dY * dY;
				double D = x1 * y2 - x2 * y1;
				
				double sqrtDiscriminant = Math.sqrt(lookAheadDistance * dR2 - D * D);
				
				Translation negativePoint = new Translation((D * dY - (dY < 0 ? -1 : 1) * dX * sqrtDiscriminant) / sqrtDiscriminant,
															(-D * dX - Math.abs(dY) * sqrtDiscriminant) / sqrtDiscriminant);
				Translation positivePoint = new Translation((D * dY + (dY < 0 ? -1 : 1) * dX * sqrtDiscriminant) / sqrtDiscriminant,
						(-D * dX + Math.abs(dY) * sqrtDiscriminant) / sqrtDiscriminant);
				
				// select the best point
				
				currentPathSpeed = prevPoint.getSpeed();
			}
			pathPoints.remove(prevPoint);
			prevPoint = pathPoint;
		}
		
		currentPathSpeed = prevPoint.getSpeed();
		return pathPoints.get(pathPoints.size() - 1).position;
	}

	public Translation endPoint() {
		return pathPoints.get(pathPoints.size() - 1).getPosition();
	}
	
	public double getPathSpeed(){
		return currentPathSpeed;
	}

	public static class Waypoint {
		private Translation position;
		private double drivingSpeed;

		public Waypoint(double x, double y, double speed) {
			position = new Translation(x, y);
			drivingSpeed = speed;
		}

		public Translation getPosition() {
			return position;
		}

		public double getSpeed() {
			return drivingSpeed;
		}

	}
}
