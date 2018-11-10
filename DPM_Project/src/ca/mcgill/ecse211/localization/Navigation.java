package ca.mcgill.ecse211.localization;

import ca.mcgill.ecse211.localization.Navigation;
import ca.mcgill.ecse211.project.SensorPoller;
import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.project.SensorPoller;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * Navigation Class
 * 
 * @author aarud
 *
 */

public class Navigation {
	
		private static Odometer odometer;
		static EV3LargeRegulatedMotor leftMotor;
		static EV3LargeRegulatedMotor rightMotor;
		private static double TRACK;
		private static double leftRadius;
		private static double rightRadius; 
		private static final int FORWARD_SPEED = 140;
		private static final int ROTATE_SPEED = 85;
		private static int llx, lly, urx, ury;
		private static int sc;
		private static final double TILE_SIZE = 30.48;
		private static double prevAngle;
		public static final double WHEEL_RAD = 2.2;
		private static boolean turnLeft = false;
		private static double deltaAngle = 0;
		public static boolean isAvoiding = false;
		public static boolean isTurning = false;
		public static int currentDestinationIndex = -1;
		public static double[]odometerData = {0,0,0};
		public static boolean navigationFinish = false;
		public static boolean shouldAvoid = false;
		
		
		//public void run(EV3LargeRegulatedMotor left, EV3LargeRegulatedMotor right,
		//		double radius, double track, Odometer odom, int LLx, int LLy, int URx, int URy, int SC) {
		public void run(EV3LargeRegulatedMotor left, EV3LargeRegulatedMotor right,
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
			navigationFinish = true;
//			left.stop();
//			right.stop();
	}
		
		public static void travelTo(double x, double y) {
				// Define variables
				//SensorPoller.filter = 0;
				double absAngle = 0, disToTravel = 0, deltaX = 0, deltaY = 0;			
			
				odometerData = odometer.getXYT();
			
				/*
				odometerData[0] = odometer.getX();
				odometerData[1] = odometer.getY();
				odometerData[2] = odometer.getTheta();
				*/
				currentDestinationIndex++;
					
				x = x * TILE_SIZE;
				y = y * TILE_SIZE;

				// Set odometer reading angle as prev angle as well
				prevAngle = odometerData[2];

				// Get displacement to travel on X and Y axis
				deltaX = x - odometerData[0];
				deltaY = y - odometerData[1];

				// Displacement to point (hypothenuse)
				disToTravel = Math.hypot(Math.abs(deltaX), Math.abs(deltaY));

				// Get absolute angle the robot must be facing
				absAngle = Math.toDegrees(Math.atan2(deltaX, deltaY));

				// If the value of absolute angle is negative, loop it back
				/*
				if (absAngle < 0)
					absAngle = 360 - Math.abs(absAngle);
	*/
				// Make robot turn to the absolute angle
				turnTo(absAngle);

				// Set robot speed
				leftMotor.setSpeed(FORWARD_SPEED);
				rightMotor.setSpeed(FORWARD_SPEED);

				// Move distance to the waypoint after robot has adjusted angle
				leftMotor.rotate(convertDistance(leftRadius, disToTravel), true);
				rightMotor.rotate(convertDistance(rightRadius, disToTravel), false);
				
				while (Navigation.shouldAvoid);	//MoveFollowingLine.avoid();
				/*
				if (!navigationFinish) {
				leftMotor.setSpeed(ROTATE_SPEED);
				rightMotor.setSpeed(ROTATE_SPEED);
				leftMotor.forward();
				rightMotor.forward();
				*/
				/*
				if (absAngle > 80 && absAngle < 100) {
				LightLocalizer.lightSensorCorrection();
				}
				*/
				/*
				leftMotor.setSpeed(ROTATE_SPEED);
				rightMotor.setSpeed(ROTATE_SPEED);
				if (turnLeft) {
					leftMotor.rotate(-convertAngle(leftRadius, TRACK, -deltaAngle), true);
					rightMotor.rotate(convertAngle(rightRadius, TRACK, -deltaAngle), false);
				} else {
					leftMotor.rotate(convertAngle(leftRadius, TRACK, -deltaAngle), true);
					rightMotor.rotate(-convertAngle(rightRadius, TRACK, -deltaAngle), false);
				}
				*/
				Sound.beep();
				//}
				odometerData[0] = 0;
				odometerData[1] = 0;
				odometerData[2] = 0;
				Sound.beepSequenceUp();
			//	}
		}
		/**
		 * This method causes the robot to turn (on point) to the absolute heading theta
		 */
		public static void turnTo(double theta) {
			isTurning = true;

			//Ensure the angle is between 0 and 360
			if (theta < 0)
				theta = theta + 360;

			boolean shouldTurnCounterclockwise = false;

			double deltaTheta = 0;
			// We only need to rotate by the difference of the final angle and the current angle
			deltaTheta = theta - prevAngle;

			//Ensure the angle is between 0 and 360
			if (deltaTheta < 0) {
				deltaTheta = deltaTheta + 360;
			}

			// Check if we should make our turn clockwise or counterclockwise
			if (deltaTheta < 180) {
				shouldTurnCounterclockwise = false;
			} else {
				shouldTurnCounterclockwise = true;
				deltaTheta = 360 - Math.abs(deltaTheta);
			}

			// Set slower rotate speed
			leftMotor.setSpeed(ROTATE_SPEED);
			rightMotor.setSpeed(ROTATE_SPEED);

			// Turn motors according to which direction we want to turn in
			if (shouldTurnCounterclockwise) {
				leftMotor.rotate(-convertAngle(leftRadius, TRACK, deltaTheta), true);
				rightMotor.rotate(convertAngle(rightRadius, TRACK, deltaTheta), false);
			} else {
				leftMotor.rotate(convertAngle(leftRadius, TRACK, deltaTheta), true);
				rightMotor.rotate(-convertAngle(rightRadius, TRACK, deltaTheta), false);
			}
			isTurning = false;
			
			
		}
		
		private static int convertDistance(double radius, double distance) {
			return (int) ((180.0 * distance) / (Math.PI * radius));
		}

		private static int convertAngle(double radius, double width, double angle) {
			return convertDistance(radius, Math.PI * width * angle / 360.0);
		}
	
	
//	/**
//	 * Method takes in x, y coordinates and finds shortest
//	 * travel distance to get to inputed coordinates
//	 * 
//	 * @param x
//	 * @param y
//	 */
//	public static void travelTo(double x, double y) {
//		
//	}
//
//	/**
//	 * takes an input angle value for Theta, and uses it to perform an angel correction in order to 
//	 * turn at minimal angle
//	 * 
//	 * @param theta
//	 */
//	public static void turnTo(double theta) {
//		
//	}
//	
//	/**
//	 * convert the distance need to travel to the rotation of the wheel	 
//	 * 
//	 * @param distance
//	 * @param wheelRadius
//	 * @return
//	 */
//	private static int convertDistance(double radius, double distance) {
//		return (int) ((180.0 * distance) / (Math.PI * radius));
//	}
//
//	
//	/**
//	 * 	 
//	 *convert the angle you enter to an angle the wheel needs to rotate
//	 *
//	 * @param angle
//	 * @param track
//	 * @param wheelRadius
//	 * @return
//	 */
//	private static int convertAngle(double radius, double width, double angle) {
//		return convertDistance(radius, Math.PI * width * angle / 360.0);
//	}
}


