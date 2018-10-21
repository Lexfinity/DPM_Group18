package ca.mcgill.ecse211.lab4;

import ca.mcgill.ecse211.odometer.Odometer;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;


/**
 * This class is meant as a skeleton for the ultrasonic sensor localization class to be used.
 * 
 * @author Zikun Lyu
 */
public class UltrasonicLocalizer {

	private static int distance; // distance reading from the ultrasonic sensor
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


	public static void run(EV3LargeRegulatedMotor left, EV3LargeRegulatedMotor right,
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
	    
		distance = UltrasonicPoller.getDistance();

		if (distance > Lab5.TILE_SIZE) {
			fallingEdge();
		}
		else {
			risingEdge();
		}

		//After the two rotations, computer the current heading
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
			distance = UltrasonicPoller.getDistance();
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
			distance = UltrasonicPoller.getDistance();
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
			distance = UltrasonicPoller.getDistance();
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
			distance = UltrasonicPoller.getDistance();
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
