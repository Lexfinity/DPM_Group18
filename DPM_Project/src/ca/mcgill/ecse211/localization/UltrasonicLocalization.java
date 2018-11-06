package ca.mcgill.ecse211.localization;

import ca.mcgill.ecse211.odometer.Odometer;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 *  Ultrasonic Localization class
 *  
 * @author aarud
 *
 */
public class UltrasonicLocalization {
	
	
	/**
	 * Constructor for UltrasonicLocalization object
	 * 
	 * @param leftMotor
	 * @param rightMotor
	 * @param odometer
	 */
	public UltrasonicLocalization(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, Odometer odometer) {
		
		
	}
	
	/**
	 * takes an input angle value for Theta, and uses it to perform an angel correction in order to 
	 * turn at minimal angle
	 * 
	 * @param theta
	 */
	public void turnTo(double theta) {
		
	}
	

}
