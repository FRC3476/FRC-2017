package org.usfirst.frc.team3476.utility;

import java.util.ArrayList;
import java.util.List;

public class CircularQueue <T> {
	
	List<T> queue;
	long back;
	int size;
	
	public CircularQueue (int size) {
		queue = new ArrayList<T>(size);
		back = 0;
		this.size = size;
	}
	
	public void add (T t) {
		queue.set((int) back % size, t);
		back++;
	}
	
	public T get (int position) {
		return queue.get((int) (back - position - 1) % size);
	}
}