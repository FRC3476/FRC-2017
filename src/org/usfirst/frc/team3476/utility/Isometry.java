package org.usfirst.frc.team3476.utility;

public class Isometry {
	
	Rotation2D rotationVector;
	Translation2D translationVector;	
	
	public Isometry(){
		rotationVector = new Rotation2D();
		translationVector = new Translation2D();
	}
	
	public Isometry(Rotation2D rotation, Translation2D translation){
		this.rotationVector = rotation;
		this.translationVector = translation;
	}
	
}
