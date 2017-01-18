package org.usfirst.frc.team3476.utility;

public class Toggle {
	boolean oldVal;
	boolean newVal;
	
	public Toggle(){
		oldVal = false;
		newVal = false;
	}
	
	public void input(boolean input){
		oldVal = newVal;
		newVal = input;
	}
	
	public boolean rising(){
		if(!oldVal && newVal){
			return true;
		}
		return false;
	}
	
	public boolean falling(){
		if(oldVal && !newVal){
			return true;
		}
		return false;
	}
}
