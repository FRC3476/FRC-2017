package org.usfirst.frc.team3476.auto;

import java.util.Arrays;
import java.util.List;

import edu.wpi.first.wpilibj.DriverStation;

public final class Runner implements Action {

	public enum ActionType {
		SEQUENTIAL, PARALLEL, NOWAITPARALLEL
	}

	private ActionType actionType;
	private List<Action> actionList;

	private boolean isDone = false;

	public Runner(ActionType type, Action... actions) {
		actionType = type;
		actionList = Arrays.asList(actions);
	}

	@Override
	public boolean isDone() {
		return isDone;
	}

	private void parallel() {
		for (Action action : actionList) {
			action.start();
		}

		for (Action action : actionList) {

			while (!action.isDone()) {
				if (DriverStation.getInstance().isDisabled()) {
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

	private void sequential() {
		System.out.println("sequential");
		for (Action action : actionList) {
			action.start();
			while (!action.isDone()) {
				if (DriverStation.getInstance().isDisabled()) {
					break;
				}
			}
		}
		isDone = true;
	}

	@Override
	public void start() {
		isDone = false;
		if (actionType == ActionType.SEQUENTIAL) {
			sequential();
		} else if (actionType == ActionType.PARALLEL) {
			parallel();
		} else {
			parallelNoWait();
		}

	}
}