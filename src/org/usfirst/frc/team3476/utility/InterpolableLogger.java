package org.usfirst.frc.team3476.utility;

import java.util.ArrayList;
import java.util.List;

public class InterpolableLogger<T>
{
	List<T> runningLog;
	
	public InterpolableLogger(int numValues){
		runningLog = new ArrayList<T>();
	}
	

}
