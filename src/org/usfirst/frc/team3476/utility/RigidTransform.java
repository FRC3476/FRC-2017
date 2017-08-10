package org.usfirst.frc.team3476.utility;

public class RigidTransform implements Interpolable<RigidTransform> {

	public Rotation rotationMat;
	public Translation translationMat;

	public RigidTransform() {
		rotationMat = new Rotation();
		translationMat = new Translation();
	}

	public RigidTransform(Translation translation, Rotation rotation) {
		rotationMat = rotation;
		translationMat = translation;
	}

	@Override
	public RigidTransform interpolate(RigidTransform other, double percentage) {

		return null;
	}

	public RigidTransform transform(RigidTransform delta) {
		return new RigidTransform(translationMat.translateBy(delta.translationMat.rotateBy(rotationMat)),
				rotationMat.rotateBy(delta.rotationMat));
	}
}
