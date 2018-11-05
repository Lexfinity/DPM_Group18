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
	 * Constructor for ultrasonic localizer
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
	
	/**
	 * convert the distance need to travel to the rotation of the wheel	 
	 * 
	 * @param distance
	 * @param wheelRadius
	 * @return
	 */
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	
	/**
	 * Takes in the angle, the distance of the track and the wheel radius 
	 * and converts the angle into a distance value based on the track, then
	 * passes these values within the convert distance method.
	 *	 
	 *convert the angle you enter to an angle the wheel needs to rotate
	 *
	 * @param angle
	 * @param track
	 * @param wheelRadius
	 * @return
	 */
	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
	

}
