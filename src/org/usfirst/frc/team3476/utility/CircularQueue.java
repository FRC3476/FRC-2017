package org.usfirst.frc.team3476.utility;

public class CircularQueue<D extends Interpolable<D>> {
	/*
	 * TimeStampedDatahis class is thread safe so there is no need to
	 * synchronize when using it TimeStampedDatahis class assumes the data added
	 * is sorted! O(1) add and O(log n) search
	 */

	private InterpolableValue<D>[] queue;
	private long back;
	public final int size;

	@SuppressWarnings("unchecked")
	public CircularQueue(int size) {
		queue = new InterpolableValue[size];
		back = 0;
		this.size = size - 1;
	}

	synchronized public void add(InterpolableValue<D> t) {
		queue[(int) back % size] = t;
		back++;
	}

	synchronized public InterpolableValue<D> get(int position) {
		return queue[(int) (back - position - 1) % size];
	}

	synchronized public D getKey(long time) {		
		int low = 0;
		int high = queue.length - 1; 
		while (low <= high) {
			int mid = (low + high) / 2;
			double midVal = get(mid).getKey();
			if (midVal < time) {
				low = mid + 1;
			} else if (midVal > time) {
				high = mid - 1;
			} else {
				return get(mid).getValue();
			}
		}
		double difference = time - get(low).getKey();
		double total = get(high).getKey() - get(low).getKey();
		return get(low).getValue().interpolate(get(high).getValue(), difference / total);
	}
}