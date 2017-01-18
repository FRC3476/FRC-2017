package org.usfirst.frc.team3476.utility;

import java.util.ArrayList;
import java.util.function.BiConsumer;

import edu.wpi.first.wpilibj.SpeedController;

public class MotorGroup<T extends SpeedController> {
	//DO NOT USE UNLESS THEY ARE TALONS!!!!!!!!
	private ArrayList<T> motorArray;

	@SafeVarargs
	public MotorGroup(T... args) {
		for (T motor : args) {
			motorArray.add(motor);
		}
	}

	public void add(T motor) {
		motorArray.add(motor);
	}

	public void remove(T motor) {
		motorArray.remove(motor);
	}

	public <E> void call(BiConsumer<T, E> f, E value) {
		for (T motor : motorArray) {
			f.accept(motor, value);
		}
	}
}
