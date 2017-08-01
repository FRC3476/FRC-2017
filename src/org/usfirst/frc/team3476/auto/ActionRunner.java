package org.usfirst.frc.team3476.auto;

public final class ActionRunner {

	private ActionRunner() {
	}

	public static void waitTillDone(Action... actions) {
		for (Action action : actions) {
			action.start();
		}

		for (Action action : actions) {

			while (!action.isDone()) {
				// do nothing
			}
		}
	}

	public static void sequential(Action...actions) {
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

	public static void parallel(Action...actions) {
		for (Action action : actions) {
			action.start();
		}

	}

	public static void parallel(Action action) {
		action.start();
	}
}