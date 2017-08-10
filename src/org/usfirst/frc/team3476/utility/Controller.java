package org.usfirst.frc.team3476.utility;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;

public class Controller extends Joystick {

	public static class Xbox {
		public static int A = 1;
		public static int B = 2;
		public static int X = 3;
		public static int Y = 4;
		public static int LeftBumper = 5;
		public static int RightBumper = 6;
		public static int Back = 7;
		public static int Start = 8;
		public static int LeftClick = 9;
		public static int RightClick = 10;

		public static int LeftX = 0;
		public static int LeftY = 1;
		public static int LeftTrigger = 2;
		public static int RightTrigger = 3;
		public static int RightX = 4;
		public static int RightY = 5;
	}

	private int oldButtons;

	private int currentButtons;

	public Controller(int port) {
		super(port);
	}

	public boolean getFallingEdge(int button) {
		if (button > 0 || button <= DriverStation.getInstance().getStickButtonCount(getPort())) {
			boolean oldVal = ((0x1 << (button - 1)) & oldButtons) != 0;
			boolean currentVal = ((0x1 << (button - 1)) & currentButtons) != 0;

			if (oldVal == true && currentVal == false) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public boolean getRisingEdge(int button) {
		if (button > 0 || button <= DriverStation.getInstance().getStickButtonCount(getPort())) {
			boolean oldVal = ((0x1 << (button - 1)) & oldButtons) != 0;
			boolean currentVal = ((0x1 << (button - 1)) & currentButtons) != 0;

			if (oldVal == false && currentVal == true) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public void update() {
		oldButtons = currentButtons;
		currentButtons = DriverStation.getInstance().getStickButtons(getPort());
	}

}
