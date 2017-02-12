package org.usfirst.frc.team3476.utility;

import java.util.List;
import java.util.function.Consumer;

public final class SerialThread
{
	List<Consumer<?>> function;
	
	private SerialThread(){
		
	}
	
	public static <T> void Sequential(List<Consumer<T>> function, List<T> inputs){
		
	}
}