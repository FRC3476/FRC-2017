package org.usfirst.frc.team3476.utility;

import edu.wpi.first.wpilibj.Joystick;

public class Controller extends Joystick{
	//instantiates array of button values
	boolean buttons[] = new boolean[super.getButtonCount()];
	boolean lastButtons[] = new boolean[buttons.length];
	
	//instantiates array of axes values
	double axes[] = new double[super.getAxisCount()];
	double lastAxes[] = new double[axes.length];
	
	
	public Controller(int port) {
		super(port);
	}

	public void update() {
		//update buttons
		for(int i = 0; i < buttons.length; i++) {
			lastButtons[i] = buttons[i];
			buttons[i] = super.getRawButton(i);
		}
		
		//update axes
		for(int i = 0; i < axes.length; i++) {
			lastAxes[i] = axes[i];
			axes[i] = -super.getRawAxis(i);
		}
	}
	
	public boolean getRisingEdge(int button) {
		//returns true when the value of the button changes from false to true
		if(lastButtons[button] == false && buttons[button] == true )
			return true;
		return false;
	}
	
	public boolean getFallingEdge(int button) {
		//returns true when the value of the button changes from true to false
		if(lastButtons[button] == true && buttons[button] == false)
			return true;
		return false;
	}
	
	public double getRawAxis(int axis) {
		//new method as the original method returned values inverted
		return axes[axis];
	}
}
