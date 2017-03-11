package org.usfirst.frc.team3476.auto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.usfirst.frc.team3476.auto.Action;

import edu.wpi.first.wpilibj.DriverStation;

public final class Runner implements Action{

	private ActionType actionType;
	private List<Action> actionList;
	private boolean isDone = false;
	
	public Runner(ActionType type, Action ...actions ) {
		actionType = type;
		actionList = Arrays.asList(actions);
	}

	public enum ActionType {SEQUENTIAL, PARALLEL, NOWAITPARALLEL}
	
	private void parallel() {
		for (Action action : actionList) {
			action.start();
		}

		for (Action action : actionList) {

			while (!action.isDone()) {
				if(DriverStation.getInstance().isDisabled()){
					break;
				}
			}
		}
		isDone = true;
	}

	private void sequential() {
		System.out.println("sequential");
		for (Action action : actionList) {
			action.start();
			while (!action.isDone()) {
				if(DriverStation.getInstance().isDisabled()){
					break;
				}
			}
		}
		isDone = true;
	}


	private void parallelNoWait() {
		System.out.println("parallel");
		for (Action action : actionList) {
			action.start();
		}
		isDone = true;
	}

	@Override
	public void start()
	{
		isDone = false;
		if (actionType == ActionType.SEQUENTIAL)
			sequential();
		else if (actionType == ActionType.PARALLEL)
			parallel();
		else
			parallelNoWait();
		
	}

	@Override
	public boolean isDone()
	{
		return isDone;
	}
}