package org.usfirst.frc.team3476.utility;

//https://github.com/Team254/FRC-2016-Public/blob/master/src/com/team254/lib/util/InverseInterpolable.java

public interface InverseInterpolable<T>{
	
	public double inverseInterpolate(T upper, T query);
	
}
