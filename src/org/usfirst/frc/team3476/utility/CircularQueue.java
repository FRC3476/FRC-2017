package org.usfirst.frc.team3476.utility;

import java.util.ArrayList;
import java.util.List;

public class CircularQueue <T> {
	
	T[] queue;
	long back;
	int size;
	
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