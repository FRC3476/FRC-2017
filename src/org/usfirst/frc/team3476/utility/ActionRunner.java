package org.usfirst.frc.team3476.utility;

import java.util.List;

public final class ActionRunner {
	
	private ActionRunner(){		
	}
	
	public static void sequentialRun(List<Action> actions){
		for(Action action : actions){
			action.start();
			while(!action.isDone()){
				
			}
		}
	}
	
	public static void sequentialRun(Action action){
		action.start();
		while(!action.isDone()){
			//do nothing
		}
	}
	
	public static void parallelRun(List<Action> actions){
		for(Action action : actions){
			action.start();
		}
	}
	
	public static void parallelRun(Action action){
		action.start();
	}
}