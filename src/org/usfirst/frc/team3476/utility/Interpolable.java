package org.usfirst.frc.team3476.utility;

public interface Interpolable <T extends Interpolable<T>>{
	
	public T interpolate(T other, double percentage);
}
