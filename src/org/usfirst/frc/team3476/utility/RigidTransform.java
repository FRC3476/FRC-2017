package org.usfirst.frc.team3476.utility;

public class RigidTransform {
	
	private Rotation rotationMat;
	private Translation translationMat;	
	
	public RigidTransform(){
		rotationMat = new Rotation();
		translationMat = new Translation();
	}
	
	public RigidTransform(Rotation rotation, Translation translation){
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


