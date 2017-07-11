package org.usfirst.frc.team3476.utility;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;

public class Controller extends Joystick {

	private int oldButtons;
	private int currentButtons;

	public Controller(int port) {
		super(port);
	}

	public void update() {
		oldButtons = currentButtons;
		currentButtons = DriverStation.getInstance().getStickButtons(getPort());
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

}
