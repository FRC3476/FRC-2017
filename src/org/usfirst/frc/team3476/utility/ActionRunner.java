package org.usfirst.frc.team3476.utility;

import java.util.List;
import java.util.function.Consumer;

public final class ActionRunner {
	
	private ActionRunner(){		
	}
	
	public static void sequentialRun(List<Action> actions){
		for(Action action : actions){
			action.start();
			while(!action.isDone()){
				//do nothing
			}
		}
	}
	
	public static void sequentialRun(Action action){
		action.start();
		while(!action.isDone()){
			//do nothing
		}
	}
	
	public static void parrallelRun(List<Action> actions){
		for(Action action : actions){
			action.start();
		}
	}
	
	public static void parrallelRun(Action action){
		action.start();
	}
}