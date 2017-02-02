package org.usfirst.frc.team3476.utility;


public class Rotation {
	
	private double cos;
	private double sin;
	
	public Rotation(){
		cos = 1;
		sin = 1;
	}
	
	public Rotation(double cos, double sin){
		this.cos = cos;
		this.sin = sin;
	}
	
	public double getDegrees(){
		return Math.toDegrees(getRadians());
	}
	
	// cos , sin in unit circle
	public double getRadians(){
		return Math.atan2(sin, cos);
	}
	
	// Rotation matrix consists of
	// cos O -sin O
	// sin O  cos O
	// R = rotation matrix 
	// Rotated coordinates is R * R
	// We only need cos O and sin O because the other two can be determined
	public Rotation rotateBy(Rotation rotationMat){
		cos = cos * rotationMat.cos - sin * rotationMat.sin;
		sin = sin * rotationMat.cos + cos * rotationMat.sin;
		
		return new Rotation(cos, sin);
	}
	
	public Rotation inverse(){
		return new Rotation(-cos, -sin);
	}
	
	public double cos(){
		return cos;
	}
	
	public double sin(){
		return sin;
	}
}