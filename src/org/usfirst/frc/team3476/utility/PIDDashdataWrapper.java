package org.usfirst.frc.team3476.utility;

import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

public class PIDDashdataWrapper implements PIDSource {
	private String keypath;
	private boolean stale;
	private long lastDataTime;

	public PIDDashdataWrapper(String keypath) {
		this.keypath = keypath;
		stale = true;
		lastDataTime = System.nanoTime();
	}

	@Override
	public double pidGet() {
		return getValue(keypath);
	}

	public double getValue(String keypath) {
		return Dashcomm.get(keypath, 0);
	}

	public boolean checkFrameDouble() {
		boolean newframe = Dashcomm.get("data/newframe", false);
		if (newframe) {
			Dashcomm.put("data/newframe", false);
			return true;
		}
		return false;
	}

	public boolean checkFrame() {
		boolean newframe = Dashcomm.get("data/newframe", false);
		if (newframe) {
			return true;
		}
		return false;
	}

	@Override
	public void setPIDSourceType(PIDSourceType pidSource) {
	}

	@Override
	public PIDSourceType getPIDSourceType() {
		return PIDSourceType.kDisplacement;
	}

}
