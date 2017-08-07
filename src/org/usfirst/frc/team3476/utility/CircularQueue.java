package org.usfirst.frc.team3476.utility;

public class CircularQueue <T> {
	/*
	 * This class is thread safe so there is no need to synchronize when using it
	 */
	
	private T[] queue;
	private long back;
	public final int size;
	
	@SuppressWarnings("unchecked")
	public CircularQueue (int size) {
		queue = (T[])new Object[size];
		back = 0;
		this.size = size - 1;
	}
	
	synchronized public void add (T t) {
		queue[(int) back % size] = t;
		back++;
	}
	
	synchronized public T get (int position) {
		return queue[(int) (back - position - 1) % size];
	}
}