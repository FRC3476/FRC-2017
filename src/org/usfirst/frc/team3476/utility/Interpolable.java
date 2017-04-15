package org.usfirst.frc.team3476.utility;

import java.util.Map.Entry;
import java.util.TreeMap;

public class Interpolable {
	
	private TreeMap<Double, Double> sortedList;
	
	public Interpolable(){
		sortedList = new TreeMap<Double, Double>();
	}
	
	public synchronized Double interpolate(Double key){
		if(sortedList.containsKey(key)){
			return sortedList.get(key);
		}
		Entry<Double, Double> highest = sortedList.ceilingEntry(key);
		Entry<Double, Double> lowest = sortedList.floorEntry(key);
		if(lowest == null && highest == null){
			return 10.0;
		} else if(lowest == null){
			return 10.0;
		} else if (highest == null){
			return 10.0;
		} else {
			double percentage = (key - lowest.getKey()) / (highest.getKey() - lowest.getKey());
			return percentage * (highest.getValue() - lowest.getValue()) + lowest.getValue();
		}
	}
	
	public synchronized void addNumber(Double key, Double value){
		sortedList.put(key, value);
	}
}
