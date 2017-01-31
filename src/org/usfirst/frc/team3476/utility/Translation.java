package org.usfirst.frc.team3476.utility;

public class Translation {
	
	double x;
	double y;
	
	public Translation(){	
		x = 0;
		y = 0;
	}
	
	public Translation(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	public void setX(double x){
		this.x = x;
	}
	
	public void setY(double y){
		this.y = y;
	}
	
	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}
	
	public void translateBy(double x, double y){
		this.x += x;
		this.y += y;
	}
	
	public void rotateBy(Rotation rotationAngle){
		// TODO: Multiply that shit
	}
}
