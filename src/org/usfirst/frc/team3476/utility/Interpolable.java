package org.usfirst.frc.team3476.utility;

//https://github.com/Team254/FRC-2016-Public/blob/master/src/com/team254/lib/util/Interpolable.java

public interface Interpolable<T> {

	public T interpolate(T other, double x);
	
}
