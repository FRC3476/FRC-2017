package org.usfirst.frc.team3476.utility;

public class RigidTransform {
	
	public Rotation rotationMat;
	public Translation translationMat;	
	
	public RigidTransform(){
		rotationMat = new Rotation();
		translationMat = new Translation();
	}
	
	public RigidTransform(Rotation rotation, Translation translation){
		this.rotationMat = rotation;
		this.translationMat = translation;
	}
		
	public RigidTransform transform(RigidTransform delta){
				
	}
}


