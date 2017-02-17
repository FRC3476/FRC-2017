package org.usfirst.frc.team3476.auto;

import java.util.List;

public final class ActionRunner {

	private ActionRunner() {
	}

	public static void waitTillDone(List<Action> actions) {
		for (Action action : actions) {
			action.start();
		}

		for (Action action : actions) {

			while (!action.isDone()) {
				// do nothing
			}
		}
	}

	public static void sequential(List<Action> actions) {
		for (Action action : actions) {
			action.start();
			while (!action.isDone()) {
				// do nothing
			}
		}
	}

	public static void sequential(Action action) {
		action.start();
		while (!action.isDone()) {
			// do nothing
		}
	}

	public static void parallel(List<Action> actions) {
		for (Action action : actions) {
			action.start();
		}

	}

	public static void parallel(Action action) {
		action.start();
	}
}