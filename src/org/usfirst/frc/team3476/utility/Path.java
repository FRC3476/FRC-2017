package org.usfirst.frc.team3476.utility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.usfirst.frc.team3476.utility.Path.Waypoint;


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
	
	public synchronized double update(Translation robotPose){
		Waypoint prevPoint = pathPoints.get(0);
		Waypoint currentPoint = pathPoints.get(1);	
		Rotation pathAngle = prevPoint.getPosition().getAngleTo(currentPoint.getPosition());
		Translation robotToPath = robotPose.rotateBy(pathAngle.inverse());
		prevPoint.setPosition(new Translation(prevPoint.getPosition().getX(), robotToPath.getY()).rotateBy(pathAngle));
		double distance = Math.abs(robotToPath.getX() - prevPoint.getPosition().getX());
		return distance;		
	}

	public synchronized Translation getLookAheadPoint(Translation robotPose, double lookAheadDistance) {
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
				currentPathSpeed = pathPoint.getSpeed();
				Translation startToEnd = prevPoint.getPosition().inverse().translateBy(pathPoint.getPosition());
				Translation startToNegative = prevPoint.getPosition().inverse().translateBy(negativePoint);
				Translation startToPositive = prevPoint.getPosition().inverse().translateBy(positivePoint);
				double negDotProduct = startToEnd.getX() * startToNegative.getX() + startToEnd.getY() * startToNegative.getY();	
				double posDotProduct = startToEnd.getX() * startToPositive.getX() + startToEnd.getY() * startToPositive.getY();			
				if (posDotProduct < 0 && negDotProduct >= 0) {
					return negativePoint;
				} else if (posDotProduct >= 0 && negDotProduct < 0) {
					return positivePoint;
				} else {
					if (Math.abs(posDotProduct) <= Math.abs(negDotProduct)) {
						return positivePoint;
					} else {
						return negativePoint;
					}
				}

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
	
	public synchronized double getPathSpeed(){
		return currentPathSpeed;
	}

	public static class Waypoint {
		private Translation position;
		private double drivingSpeed;

		public Waypoint(double x, double y, double speed) {
			position = new Translation(x, y);
			drivingSpeed = speed;
		}

		public synchronized Translation getPosition() {
			return position;
		}

		public synchronized void setPosition(Translation newPosition){
			position = newPosition;
		}
		
		public double getSpeed() {
			return drivingSpeed;
		}

	}
}
