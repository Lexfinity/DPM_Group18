package ca.mcgill.ecse211.localization;

/**
 * Navigation Class
 * 
 * @author aarud
 *
 */

public class Navigation {
	
	
	/**
	 * Method takes in x, y coordinates and finds shortest
	 * travel distance to get to inputed coordinates
	 * 
	 * @param x
	 * @param y
	 */
	public static void travelTo(double x, double y) {
		
	}

	/**
	 * takes an input angle value for Theta, and uses it to perform an angel correction in order to 
	 * turn at minimal angle
	 * 
	 * @param theta
	 */
	public static void turnTo(double theta) {
		
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
