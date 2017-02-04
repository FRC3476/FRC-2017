package org.usfirst.frc.team3476.utility;

import java.util.ArrayList;

public class Path {

	// TODO: Create Path thing
	// Needs to be able to interpolate
	private ArrayList<Translation> pathPoints;
	
	
	public Path(Translation initialPoint){
		pathPoints.add(initialPoint);
	}
	
	public void addWaypoint(Translation nextPoint){
		pathPoints.add(nextPoint);
	}
	
	public double getRadius(Translation robotPosition, double lookAheadDistance){
		Translation closestPoint = getClosestPoint(robotPosition, lookAheadDistance);
		double angleOffset = Math.asin(lookAheadDistance / getDistanceTo(closestPoint, robotPosition));
		Translation leftRight = robotPosition.rotateBy(new Rotation(Math.cos(angleOffset), Math.sin(angleOffset)));
		Translation pointOnPath = new Translation(lookAheadDistance / Math.tan(angleOffset), 0);
		Translation lookAheadPoint = pointOnPath.rotateBy(new Rotation(Math.cos(-angleOffset), Math.sin(-angleOffset)));
		// TODO: turn left or right???
		if(leftRight.getY() < 0){
			return Math.pow(getDistanceTo(lookAheadPoint, robotPosition), 2) / (2 * pointOnPath.getX());
		}
		return -1 * Math.pow(getDistanceTo(lookAheadPoint, robotPosition), 2) / (2 * pointOnPath.getX());
	}
	
	public double getDistanceTo(Translation pathPoint, Translation robotPosition){
		return Math.sqrt(Math.pow((pathPoint.getX() - robotPosition.getX()), 2) + Math.pow(pathPoint.getY() - robotPosition.getY(), 2));
	}
	
	public Translation getClosestPoint(Translation robotPosition, double lookAheadDistance){
		for(Translation pathPoint : pathPoints){
			if(lookAheadDistance <= getDistanceTo(pathPoint, robotPosition)){			
				return pathPoint;
			}
		}
		return pathPoints.get(pathPoints.size() - 1);		
	}
	
	public Rotation getAngle(Translation currentPoint, Translation nextPoint){
		double angleOffset = Math.asin(currentPoint.getY() - nextPoint.getY()/getDistanceTo(currentPoint, nextPoint));
		return new Rotation(Math.cos(angleOffset), Math.sin(angleOffset));
	}
	
	
}

