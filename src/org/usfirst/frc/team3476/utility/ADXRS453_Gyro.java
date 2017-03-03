package org.usfirst.frc.team3476.utility;

import java.util.Arrays;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.SPI;

public class ADXRS453_Gyro extends ADXRS450_Gyro 
{
	public final int DEFSAMPLES = 5;
	private int samples;
	
	public ADXRS453_Gyro(SPI.Port port)
	{
		super(port);
		super.calibrate();
		samples = DEFSAMPLES;
	}
	
	public ADXRS453_Gyro(SPI.Port port, int numSamples)
	{
		super(port);
		samples = numSamples;
	}

	private double lastHeading;
	
	public double calcDiff()
	{
		return get() - lastHeading;
	}
	
	public void resetDiff()
	{
		lastHeading = get();
	}
	
	public double pidGet()
	{
		return getAngle();
	}
	
	public double getAngle()
	{
		return calcDiff();
	}
	
	public double get()
	{
		double[] data = new double[samples];
		
		for(int i = 0; i < samples; i++)
		{
			data[i] = super.getAngle();
		}
		
		Arrays.sort(data);
		
		return data[(int)(samples/2)];
	}
}