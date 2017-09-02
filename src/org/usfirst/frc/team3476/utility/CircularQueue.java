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
		this.size = size;
	}

	synchronized public void add(InterpolableValue<D> t) {
		queue[(int) back % size] = t;
		back++;
	}

	synchronized public InterpolableValue<D> getFromQueue(int position) {
		position %= size;
		return queue[(int) (back - position - 1) % size];
	}

	synchronized public D getInterpolatedKey(long key) {		
		int low = 0;
		int high = queue.length - 1; 
		while (low <= high) {
			int mid = (low + high) / 2;
			double midVal = getFromQueue(mid).getKey();
			if (midVal < key) {
				low = mid + 1;
			} else if (midVal > key) {
				high = mid - 1;
			} else {
				return getFromQueue(mid).getValue();
			}
		}
		double difference = key - getFromQueue(low).getKey();
		double total = getFromQueue(high).getKey() - getFromQueue(low).getKey();
		return getFromQueue(low).getValue().interpolate(getFromQueue(high).getValue(), difference / total);
	}
}