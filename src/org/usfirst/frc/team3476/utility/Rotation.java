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
	
	public void rotateBy(double angle){
		
	}
}
