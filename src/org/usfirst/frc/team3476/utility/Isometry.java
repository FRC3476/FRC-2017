package org.usfirst.frc.team3476.utility;

public class Isometry {
	
	Rotation rotationMat;
	Translation translationMat;	
	
	public Isometry(){
		rotationMat = new Rotation();
		translationMat = new Translation();
	}
	
	public Isometry(Rotation rotation, Translation translation){
		this.rotationMat = rotation;
		this.translationMat = translation;
	}
	
	public void setTranslation(Translation translationMat){
		this.translationMat = translationMat;
	}
	
	public void setRotation(Rotation rotationMat){
		this.rotationMat = rotationMat;
	}
	
	public Translation getTranslation(){
		return translationMat;
	}
	
	public Rotation getRotation(){
		return rotationMat;
	}
	
	
}
