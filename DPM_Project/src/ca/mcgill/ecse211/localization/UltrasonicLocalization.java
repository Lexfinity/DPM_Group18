package ca.mcgill.ecse211.localization;

import ca.mcgill.ecse211.project.GameController; 
import ca.mcgill.ecse211.project.SensorPoller;
import ca.mcgill.ecse211.odometer.Odometer;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 *  Ultrasonic Localization class
 *  
 * @author aarud
 *
 */
public class UltrasonicLocalization { 
	
	private static int distance; // distance reading from the ultrasonic sensor
	//private static Odometer odometer;
	private static Odometer odometer;
	private static EV3LargeRegulatedMotor leftMotor;
	private static EV3LargeRegulatedMotor rightMotor;
	private static double TRACK;
	private static double leftRadius;
	private static double rightRadius;
	private static final int d = 35; 
	private static final int k = 1;  
	private static final int ROTATE_SPEED = 85;
	private static double firstAngle; 
	private static double lastAngle; 
	private static double deltaT;
	public static boolean ultrasonicLocalizerFinish = false;


	public void run(EV3LargeRegulatedMotor left, EV3LargeRegulatedMotor right,
			double radius, double track, Odometer odom) {
		odometer = odom;
		leftMotor = left;
		rightMotor = right;
		leftRadius = radius;
		rightRadius = radius;
		TRACK = track;
		
		//Stop the motors
		left.stop();
		right.stop();
		left.setAcceleration(5000);
		right.setAcceleration(5000);
	    
	    try {
	      Thread.sleep(1500);
	    } catch (InterruptedException e) {
	    }
	    
		distance = SensorPoller.getDistance();

		if (distance > GameController.TILE_SIZE) {
			fallingEdge();
		}
		else {
			risingEdge();
		}

		//After the two rotations, compute the current heading
		if (firstAngle < lastAngle) {
			deltaT = 45 - (firstAngle + lastAngle) / 2 + 180;
		}
		else {
			deltaT = 225 - (firstAngle + lastAngle) / 2 + 180;
		}
		double diffTheta = 360 - odometer.getXYT()[2] - deltaT; 
										
		//Rotate to the "true" north
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		leftMotor.rotate(convertAngle(leftRadius, TRACK, diffTheta), true);
		rightMotor.rotate(-convertAngle(rightRadius, TRACK, diffTheta), false);
		odometer.setTheta(0); 
		ultrasonicLocalizerFinish = true; 
	}
	
	/**
	 * Rotates to the true north based on a falling edge method
	 */
	private static void fallingEdge() {
		//Rotate clockwise until we detect a falling edge
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		leftMotor.forward();
		rightMotor.backward();
		
		while(true) {
			distance = SensorPoller.getDistance();
			if (distance < d - k) {
				Sound.beep();
				firstAngle = odometer.getXYT()[2];
				leftMotor.setSpeed(0);
				rightMotor.setSpeed(0);
				break;
			}
		}
		
		//Rotate counterclockwise until we detect a second falling edge
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		leftMotor.backward();
		rightMotor.forward();
	    try {
		      Thread.sleep(1500);
		    } catch (InterruptedException e) {
		    	
		    }
		
		while(true) {
			distance = SensorPoller.getDistance();
			if (distance < d - k) {
				Sound.beep();
				lastAngle = odometer.getXYT()[2];
				leftMotor.setSpeed(0);
				rightMotor.setSpeed(0);
				break;
			}
		}    
	}
	/**
	 * Rotates to the true north based on a rising edge method
	 */
	private static void risingEdge() {
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		leftMotor.backward();
		rightMotor.forward();
		
		while(true) {
			distance = SensorPoller.getDistance();
			if (distance > d - k) {
				Sound.beep();
				firstAngle = odometer.getXYT()[2];
				leftMotor.setSpeed(0);
				rightMotor.setSpeed(0);
				break;
			}
		}
		
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		leftMotor.forward();
		rightMotor.backward();
	    try {
		      Thread.sleep(1500);
		    } catch (InterruptedException e) {
		    	
		    }
		
		while(true) {
			distance = SensorPoller.getDistance();
			if (distance > d - k) {
				Sound.beep();
				lastAngle= odometer.getXYT()[2];
				leftMotor.setSpeed(0);
				rightMotor.setSpeed(0);
				break;
			}
		}  
	}


	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
}

	
//	/**
//	 * Constructor for UltrasonicLocalization object
//	 * 
//	 * @param leftMotor
//	 * @param rightMotor
//	 * @param odometer
//	 */
//	public UltrasonicLocalization(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, Odometer odometer) {
//		
//		
//	}
//	
//	/**
//	 * 
//	 * The usLocalizer method uses the ultrasonic sensor to get the distance value from the
//	 * usSensor. It continuously takes in the distance until it reaches the rising_limit or the
//	 * falling_limit * which are the maximum and minimum distances the usSensor can have between
//	 * the wall when this distance is reached the robot stops and begins to rotate in the opposite
//	 * direction until it hits the rising_limit or falling_limit  for the second time. fallingEdge or
//	 * risingEdge routine will be determined based on the initial starting position of the robot.
//	 * 
//	 */
//	public void usLocalizer() {
//		
//	}
//	
//
//}

