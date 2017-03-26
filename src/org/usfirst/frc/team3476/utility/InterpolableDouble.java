package org.usfirst.frc.team3476.utility;

//https://github.com/Team254/FRC-2016-Public/blob/master/src/com/team254/lib/util/InterpolatingDouble.java

public class InterpolableDouble implements InverseInterpolable<InterpolableDouble>, Comparable<InterpolableDouble>{


    public Double value;
	
    public InterpolableDouble(Double value){
    	this.value = value;
    }
    
	@Override
    public double inverseInterpolate(InterpolableDouble upper, InterpolableDouble query) {
        double upper_to_lower = upper.value - value;
        if (upper_to_lower <= 0) {
            return 0;
        }
        double query_to_lower = query.value - value;
        if (query_to_lower <= 0) {
            return 0;
        }
        return query_to_lower / upper_to_lower;
    }

    @Override
    public int compareTo(InterpolableDouble other) {
        if (other.value < value) {
            return 1;
        } else if (other.value > value) {
            return -1;
        } else {
            return 0;
        }
		
    }
}
