package ca.mcgill.ecse211.localization;

import ca.mcgill.ecse211.odometer.Odometer;
import lejos.hardware.motor.EV3LargeRegulatedMotor;


/**
 * 
 * Light Localization class
 * 
 * @author aarud
 *
 */
public class LightLocalization {

	
	
	public LightLocalization(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
			Odometer odometer) {
		
}
	
	
	
	public void lightLocalize(){
		
	}
	
	
	
	/**
	 * convert the distance need to travel to the rotation of the wheel	 
	 * 
	 * @param distance
	 * @param wheelRadius
	 * @return
	 */
	private int convertDistance(double distance, double wheelRadius) {
		return (int) ((int) (180*distance) / (Math.PI*wheelRadius));
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
	private int convertAngle(double angle, double track, double wheelRadius){
		return convertDistance(Math.PI*track*angle/360, wheelRadius);
	}

}
