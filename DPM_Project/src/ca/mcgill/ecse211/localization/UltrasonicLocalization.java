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
	 * 
	 * The usLocalizer method uses the ultrasonic sensor to get the distance value from the
	 * usSensor. It continuously takes in the distance until it reaches the rising_limit or the
	 * falling_limit * which are the maximum and minimum distances the usSensor can have between
	 * the wall when this distance is reached the robot stops and begins to rotate in the opposite
	 * direction until it hits the rising_limit or falling_limit  for the second time. fallingEdge or
	 * risingEdge routine will be determined based on the initial starting position of the robot.
	 * 
	 */
	public void usLocalizer() {
		
	}
	

}
