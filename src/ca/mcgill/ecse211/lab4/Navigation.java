package ca.mcgill.ecse211.lab4;

import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.odometer.OdometerExceptions;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;


/**
 * This class is meant as a skeleton for Navigation class to be used.
 * 
 * @author Zikun Lyu
 */
public class Navigation {
	private static Odometer odometer;
	private static EV3LargeRegulatedMotor leftMotor;
	private static EV3LargeRegulatedMotor rightMotor;
	private static double TRACK;
	private static double leftRadius;
	private static double rightRadius; 
	private static final int FORWARD_SPEED = 200;
	private static final int ROTATE_SPEED = 85;
	private static int llx, lly, urx, ury;
	private static int sc;
	private static double[] odometerData;
	private static final double TILE_SIZE = 30.48;
	private static double prevAngle;
	public static final double WHEEL_RAD = 2.2;
	private static boolean turnLeft = false;
	private static double deltaAngle = 0;
	public static boolean isAvoiding = false;
	public static boolean isTurning = false;
	public static int currentDestinationIndex = -1;
	
	
	public static void run(EV3LargeRegulatedMotor left, EV3LargeRegulatedMotor right,
			double radius, double track, Odometer odom, int LLx, int LLy, int URx, int URy, int SC) {
		odometer = odom;
		leftMotor = left;
		rightMotor = right;
		leftRadius = radius;
		rightRadius = radius;
		TRACK = track;
		llx = LLx;
		lly = LLy;
		urx = URx;
		ury = URy;
		sc = SC;
		
		travelTo(llx, lly);
		 
		left.stop();
		right.stop();
}
	
	public static void travelTo(double x, double y) {
			// Define variables
			double absAngle = 0, len = 0, deltaX = 0, deltaY = 0;
			
			if (!isAvoiding) {
			/*
			try {
				odometerData = Odometer.getOdometer().getXYT();
			} catch (OdometerExceptions e) {
				e.printStackTrace();
			}
			*/
				
			currentDestinationIndex++;
				
				
			odometerData = odometer.getXYT();
			// Convert X & Y coordinates to actual length (cm)
			x = x * TILE_SIZE;
			y = y * TILE_SIZE;

			// Set odometer reading angle as prev angle as well
			prevAngle = odometerData[2];

			// Get displacement to travel on X and Y axis
			deltaX = x - odometerData[0];
			deltaY = y - odometerData[1];

			// Displacement to point (hypothenuse)
			len = Math.hypot(Math.abs(deltaX), Math.abs(deltaY));

			// Get absolute angle the robot must be facing
			absAngle = Math.toDegrees(Math.atan2(deltaX, deltaY));

			// If the value of absolute angle is negative, loop it back
			if (absAngle < 0)
				absAngle = 360 - Math.abs(absAngle);

			// Make robot turn to the absolute angle
			turnTo(absAngle);

			// Set robot speed
			leftMotor.setSpeed(FORWARD_SPEED);
			rightMotor.setSpeed(FORWARD_SPEED);

			// Move distance to the waypoint after robot has adjusted angle
			leftMotor.rotate(convertDistance(leftRadius, len), true);
			rightMotor.rotate(convertDistance(rightRadius, len), false);
			
			if (!Lab5.navigationFinish) {
			leftMotor.setSpeed(ROTATE_SPEED);
			rightMotor.setSpeed(ROTATE_SPEED);
			leftMotor.forward();
			rightMotor.forward();
			
			LightLocalizer.lightSensorCorrection();
			
			leftMotor.setSpeed(ROTATE_SPEED);
			rightMotor.setSpeed(ROTATE_SPEED);
			if (turnLeft) {
				leftMotor.rotate(-convertAngle(leftRadius, TRACK, -deltaAngle), true);
				rightMotor.rotate(convertAngle(rightRadius, TRACK, -deltaAngle), false);
			} else {
				leftMotor.rotate(convertAngle(leftRadius, TRACK, -deltaAngle), true);
				rightMotor.rotate(-convertAngle(rightRadius, TRACK, -deltaAngle), false);
			}
			Sound.beep();
			}
			
			Sound.beepSequenceUp();
			}
	}
	/**
	 * This method causes the robot to turn (on point) to the absolute heading theta
	 */
	public static void turnTo(double theta) {
		isTurning = true;
		// Get change in angle we want
		deltaAngle = theta - prevAngle;

		// If deltaAngle is negative, loop it back
		if (deltaAngle < 0) {
			deltaAngle = 360 - Math.abs(deltaAngle);
		}

		// Check if we want to move left or right
		if (deltaAngle > 180) {
			turnLeft = true;
			deltaAngle = 360 - Math.abs(deltaAngle);
		} else {
			turnLeft = false;
		}

		// Set slower rotate speed
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);

		// Turn motors according to which direction we want to turn in
		if (turnLeft) {
			leftMotor.rotate(-convertAngle(leftRadius, TRACK, deltaAngle), true);
			rightMotor.rotate(convertAngle(rightRadius, TRACK, deltaAngle), false);
		} else {
			leftMotor.rotate(convertAngle(leftRadius, TRACK, deltaAngle), true);
			rightMotor.rotate(-convertAngle(rightRadius, TRACK, deltaAngle), false);
		}
		isTurning = false;
	}
	
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

}