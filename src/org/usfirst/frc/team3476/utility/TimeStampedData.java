package org.usfirst.frc.team3476.utility;

public interface TimeStampedData<T extends TimeStampedData<T>> {
	public long getTime();
	public T interpolate(T end, double percentage);
}
