package org.usfirst.frc.team3476.utility;

import java.util.Map.Entry;
import java.util.TreeMap;

public class Interpolable {
	
	private TreeMap<Double, Double> sortedList;
	
	public Interpolable(){
		sortedList = new TreeMap<Double, Double>();
	}
	
	public Double interpolate(Double key){
		Entry<Double, Double> highest = sortedList.ceilingEntry(key);
		Entry<Double, Double> lowest = sortedList.floorEntry(key);
		if(lowest == null){
			return highest.getValue();
		} else if (highest == null){
			return lowest.getValue();
		} else {
			double percentage = (key - lowest.getKey()) / (highest.getKey() - lowest.getKey());
			return percentage * (highest.getValue() - lowest.getValue()) + lowest.getValue();
		}
	}
	
	public void addNumber(Double key, Double value){
		sortedList.put(key, value);
	}
}
