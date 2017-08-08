package org.usfirst.frc.team3476.utility;

public class CircularQueue <T extends TimeStampedData<T>> {
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
	
	synchronized public T getTime(long time){
		for(int i = 0; i < size; i++){
			if(queue[i].getTime() < time){
				long difference = time - queue[i].getTime();
				long total = queue[i - 1].getTime() - queue[i].getTime();
				return queue[i].interpolate(queue[i - 1], difference / total);
			}
		}
		return get(size);		
	}
}