package org.usfirst.frc.team3476.utility;

import java.util.ArrayList;

public class Path {

	private ArrayList<Translation> pathPoints;
	
	
	public Path(Translation initialPoint){
		pathPoints.add(initialPoint);
	}
	
	public void addWaypoint(Translation nextPoint){
		pathPoints.add(nextPoint);
	}
	
	public double getRadius(RigidTransform robotPosition, double lookAheadDistance){
		// Get point if robot was centered on 0 degrees
		Translation lookAheadPoint = getLookAheadPoint(robotPosition.translationMat, lookAheadDistance).rotateBy(robotPosition.rotationMat.inverse());		
		
		// check if it is straight ahead or not
		if(Math.abs(lookAheadPoint.getX() - robotPosition.translationMat.getX()) < 1){
			return 0;
		}
		double radius = Math.pow(getDistanceTo(lookAheadPoint, robotPosition.translationMat), 2) / (2 * lookAheadPoint.getX());
		if(lookAheadPoint.getX() > 0){
			return radius;
		} else {
			return -radius;
		}
	}
	
	public double getDistanceTo(Translation pathPoint, Translation robotPosition){
		return Math.sqrt(Math.pow((pathPoint.getX() - robotPosition.getX()), 2) + Math.pow(pathPoint.getY() - robotPosition.getY(), 2));
	}
	
	public Translation getLookAheadPoint(Translation robotPosition, double lookAheadDistance){
		Translation prevPoint = null;
		for(Translation pathPoint : pathPoints){
			if(lookAheadDistance <= getDistanceTo(pathPoint, robotPosition)){			
				if(prevPoint == null){
					return pathPoint;
				}
				
				// law of sine to find distance on path
				Rotation nextPathAngle = getAngle(prevPoint, pathPoint);
				Rotation pathPointAngle = nextPathAngle.rotateBy(getAngle(robotPosition, prevPoint));
				Rotation lookAheadAngle = new Rotation(Math.asin(getDistanceTo(robotPosition, prevPoint) * pathPointAngle.sin() / lookAheadDistance));
				Rotation pathSegmentAngle = new Rotation(180 - lookAheadAngle.getDegrees() - pathPointAngle.getDegrees());
				
				return new Translation(0, lookAheadDistance * pathSegmentAngle.sin() / pathPointAngle.sin()).rotateBy(nextPathAngle);
				
			}
			prevPoint = pathPoint;
		}
		return pathPoints.get(pathPoints.size() - 1);	
		

	}
	
	public Rotation getAngle(Translation currentPoint, Translation nextPoint){
		double angleOffset = Math.asin(currentPoint.getY() - nextPoint.getY()/getDistanceTo(currentPoint, nextPoint));
		return new Rotation(Math.cos(angleOffset), Math.sin(angleOffset));
	}
	
	public Translation endPoint(){
		return pathPoints.get(pathPoints.size() - 1);
	}
	
}

