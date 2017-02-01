package org.usfirst.frc.team3476.utility;

public class Translation {
	
	private double x;
	private double y;
	
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
	
	public Translation translateBy(Translation delta){
		this.x += delta.getX();
		this.y += delta.getY();
		
		return new Translation(x, y);
	}
	
	// Rotation matrix consists of
	// cos O -sin O
	// sin O  cos O
	// R = rotation matrix T = translation matrix
	// Rotated coordinates is R * T
	public Translation rotateBy(Rotation rotationMat){
		x = x * rotationMat.cos() - y * rotationMat.sin();
		y = x * rotationMat.sin() + y * rotationMat.cos();
		
		return new Translation(x, y);
	}
	
}
