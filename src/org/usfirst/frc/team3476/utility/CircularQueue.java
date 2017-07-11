public class CircularQueue <T> {
	
	T[] queue;
	long back;
	
	public CircularQueue (int size) {
		queue = new T[size];
		back = 0;
	}
	
	public void add (T t) {
		queue[back % queue.size] = t; 
		back++;
	}
	
	public T get (int position) {
		return queue[(back - position - 1) % queue.size];
	}
}