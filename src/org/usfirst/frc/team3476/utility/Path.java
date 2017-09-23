package org.usfirst.frc.team3476.utility;

import java.util.ArrayList;
import java.util.List;

public class Path {

	public static class PathSegment {	
		
		private Translation2d start, end, delta;
		private double maxSpeed, distanceSquared;
		
		public PathSegment(double xStart, double yStart, double xEnd, double yEnd, double maxSpeed){
			start = new Translation2d(xStart, yStart);
			end = new Translation2d(xEnd, yEnd);
			this.maxSpeed = maxSpeed;
			distanceSquared = Math.pow(xStart - xEnd, 2) + Math.pow(yStart - yEnd, 2);
			delta = end.inverse().translateBy(start);
		}
		
		private Translation2d getStart(){
			return start;
		}
		
		private Translation2d getEnd(){
			return end;
		}
		
		public Translation2d getClosestPoint(Translation2d point){
			double u = ((point.getX() - start.getX()) * (end.getX() - start.getX()) + (point.getY() - start.getY()) * (end.getY() - start.getY())) / 2;
			u = Math.max(Math.min(u, 1), 0);
			
			
		}
		
		public Translation2d getPointByDistance(){
			
		}
		
		public double getSpeed(){
			return maxSpeed;
		}
		
		public double getDistanceSquared(){
			return distanceSquared;
		}
		
		
	}
	
	private List<PathSegment> segments, points;
	
	public Path() {
		segments = new ArrayList<PathSegment>();
	}
	
	public void addPoints(){
		
	}
	
	
}
