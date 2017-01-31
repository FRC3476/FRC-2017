package org.usfirst.frc.team3476.utility;

import java.util.Queue;

import edu.wpi.first.wpilibj.PIDController.Tolerance;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.Timer;

public class SynchronousPid {

	private static int instances = 0;
	private double m_P; // factor for "proportional" control
	private double m_I; // factor for "integral" control
	private double m_D; // factor for "derivative" control
	private double m_F; // factor for feedforward term
	private double m_maximumOutput = 1.0; // |maximum output|
	private double m_minimumOutput = -1.0; // |minimum output|
	private double m_maximumInput = 0.0; // maximum input - limit setpoint to
											// this
	private double m_minimumInput = 0.0; // minimum input - limit setpoint to
											// this
	// do the endpoints wrap around? eg. Absolute encoder
	private boolean m_continuous = false;
	// the prior error (used to compute velocity)
	private double m_prevError = 0.0;
	// the sum of the errors for use in the integral calc
	private double m_totalError = 0.0;
	// the tolerance object used to check if on target
	private Tolerance m_tolerance;
	private double m_setpoint = 0.0;
	private double m_prevSetpoint = 0.0;
	private double m_error = 0.0;
	private double m_result = 0.0;
	protected PIDSource m_pidInput;
	protected PIDOutput m_pidOutput;

	public double update(double input) {
		if(m_continuous){
			if (Math.abs(m_error) > (m_maximumInput - m_minimumInput) / 2) {
		        if (m_error > 0) {
		          m_error -= (m_maximumInput - m_minimumInput);
		        } else {
		          m_error += (m_maximumInput - m_minimumInput);
		        }
		      }
		}
		if (m_pidInput.getPIDSourceType().equals(PIDSourceType.kRate)) {
			if (m_P != 0) {
				double potentialPGain = (m_totalError + m_error) * m_P;
				if (potentialPGain < m_maximumOutput) {
					if (potentialPGain > m_minimumOutput) {
						m_totalError += m_error;
					} else {
						m_totalError = m_minimumOutput / m_P;
					}
				} else {
					m_totalError = m_maximumOutput / m_P;
				}

				m_result = m_P * m_totalError + m_D * m_error + m_setpoint * m_F;
			}
		} else {
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

			m_result = m_P * m_error + m_I * m_totalError + m_D * (m_error - m_prevError)
					+ m_setpoint * m_F;
		}
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
	public double getLastResult(){
		return m_result;
	}
	
	public double getError(){
		return m_error;
	}
	
	public boolean onTarget(){
		return true;
		// TODO: actually do later
	}
}











