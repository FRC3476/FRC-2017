package org.usfirst.frc.team3476.utility;

import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;

public class SynchronousPid {

	private double m_P; // factor for "proportional" control
	private double m_I; // factor for "integral" control
	private double m_D; // factor for "derivative" control
	private double m_F; // factor for feedforward term
	private double m_maximumOutput = 1.0; // |maximum output|
	private double m_minimumOutput = -1.0; // |minimum output|
	private double m_maximumInput = 0.0; // maximum input - limit leftSetpoint to
											// this
	private double m_minimumInput = 0.0; // minimum input - limit leftSetpoint to
											// this
	// do the endpoints wrap around? eg. Absolute encoder
	private boolean m_continuous = false;
	// the prior error (used to compute velocity)
	private double m_prevError = 0.0;
	// the sum of the errors for use in the integral calc
	private double m_totalError = 0.0;
	// the tolerance object used to check if on target
	private double m_tolerance;
	private double m_setpoint = 0.0;
	private double m_error = 0.0;
	private double m_result = 0.0;
	protected PIDSource m_pidInput;
	protected PIDOutput m_pidOutput;

	// TODO: Polish, set functions, get functions
	// Input/Output Range
	// reset
	// set deadband
	// get
	
	public SynchronousPid(double P, double I, double D, double F) {
		m_P = P;
		m_I = I;
		m_D = D;
		m_F = F;
	}

	public double update(double input) {
		m_error = m_setpoint - input;
		if (m_continuous) {
			if (Math.abs(m_error) > (m_maximumInput - m_minimumInput) / 2) {
				if (m_error > 0) {
					m_error -= (m_maximumInput - m_minimumInput);
				} else {
					m_error += (m_maximumInput - m_minimumInput);
				}
			}
		}
		if (m_I != 0) {
			double potentialIGain = (m_totalError + m_error) * m_I;
			if (potentialIGain < m_maximumOutput) {
				if (potentialIGain > m_minimumOutput) {
					m_totalError += m_error;
				} else {
					m_totalError = m_minimumOutput / m_I;
				}
			} else {
				m_totalError = m_maximumOutput / m_I;
			}
		}

		m_result = m_P * m_error + m_I * m_totalError + m_D * (m_error - m_prevError) + m_setpoint * m_F;

		m_prevError = m_error;

		if (m_result > m_maximumOutput) {
			m_result = m_maximumOutput;
		} else if (m_result < m_minimumOutput) {
			m_result = m_minimumOutput;
		}
		return m_result;
	}

	public void setSetpoint(double setpoint) {
		if (m_maximumInput > m_minimumInput) {
			if (setpoint > m_maximumInput) {
				m_setpoint = m_maximumInput;
			} else if (setpoint < m_minimumInput) {
				m_setpoint = m_minimumInput;
			} else {
				m_setpoint = setpoint;
			}
		} else {
			m_setpoint = setpoint;
		}
	}
	
	public void setInputRange(double maximumInput, double minimumInput){
		m_maximumInput = maximumInput;
		m_minimumInput = minimumInput;
	}
	
	public void setOutputRange(double maximumOutput, double minimumOutput){
		m_maximumOutput = maximumOutput;
		m_minimumOutput = minimumOutput;
	}
	
	public double getLastResult() {
		return m_result;
	}

	public double getError() {
		return m_error;
	}

	public boolean isDone() {
		return m_error < m_tolerance;
	}

	public void setPIDF(double P, double I, double D, double F) {
		m_P = P;
		m_I = I;
		m_D = D;
		m_F = F;
	}
	
	public void setP(double P){
		m_P = P;
	}
	
	public void setI(double I){
		m_I = I;
	}
	
	public void setD(double D){
		m_D = D;
	}
	
	public void setF(double F){
		m_F = F;
	}

}
