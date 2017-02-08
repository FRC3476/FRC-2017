package org.usfirst.frc.team3476.utility;

import java.util.ArrayList;

public class Path {

	private ArrayList<Waypoint> pathPoints;
	
	// TODO: Path should not use translation. New class with points, rotation, and specify if one or both has to be done
	// TODO: Remake algo to match new intended behavior
	public Path(Waypoint initialPoint){
		pathPoints.add(initialPoint);
	}
	
	public void addWaypoint(Waypoint nextPoint){
		pathPoints.add(nextPoint);
	}	
	
	public Translation getLookAheadPoint(Translation robotPosition, double lookAheadDistance){
		Translation prevPoint = null;
		for(Waypoint pathPoint : pathPoints){
			if(lookAheadDistance <= pathPoint.position.getDistanceTo(robotPosition)){			
				if(prevPoint == null){
					return pathPoint.getPosition();
				}				
				// law of sine to find distance on path
				Rotation nextPathAngle = prevPoint.getAngle(pathPoint.getPosition());
				Rotation pathPointAngle = nextPathAngle.rotateBy(robotPosition.getAngle(prevPoint));
				Rotation lookAheadAngle = new Rotation(Math.asin(robotPosition.getDistanceTo(prevPoint) * pathPointAngle.sin() / lookAheadDistance));
				Rotation pathSegmentAngle = new Rotation(180 - lookAheadAngle.getDegrees() - pathPointAngle.getDegrees());
				
				return new Translation(0, lookAheadDistance * pathSegmentAngle.sin() / pathPointAngle.sin()).rotateBy(nextPathAngle);
				
			}
			prevPoint = pathPoint.getPosition();
		}
		return pathPoints.get(pathPoints.size() - 1).position;	
	}
	
	public Translation endPoint(){
		return pathPoints.get(pathPoints.size() - 1).getPosition();
	}
	
	public static class Waypoint{
		private Translation position;
		private Rotation rotation;
		private double drivingSpeed;
		
		public Waypoint (double x, double y, double speed){
			position = new Translation(x, y);
			rotation = null;
			drivingSpeed = speed;
		}		
		
		public Translation getPosition(){
			return position;
		}
		
		public double getSpeed(){
			return drivingSpeed;
		}
		
		//optional rotation
		public Rotation getRotation(){
			return rotation;
		}
		
		
	}
}

