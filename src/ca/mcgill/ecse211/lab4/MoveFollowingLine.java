package ca.mcgill.ecse211.lab4;

import ca.mcgill.ecse211.odometer.Odometer;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;


/**
 * This class is meant as a skeleton for the MoveFollowingLine class to be used.
 * 
 * @author Zikun Lyu
 */
public class MoveFollowingLine {
	//private Odometer odometer;
	private static EV3LargeRegulatedMotor leftMotor;
	private static EV3LargeRegulatedMotor rightMotor;
	private static double TRACK;
	private static double WRadius;
	private static final int ROTATE_SPEED = 140;
	private static float color = 0;
	private static final double dis = 12.5; 
	public static boolean lightLocalizerFinish = false;
	private static int tr;
	private static int llx, lly, urx, ury;
	private static int sc;
	private static int disX;
	private static int disY;
	//public static double distance;
	public static boolean isTurning = false;
	public static int[][] destination;
	
	
	public MoveFollowingLine(EV3LargeRegulatedMotor left, EV3LargeRegulatedMotor right,
			double radius, double track, Odometer odom, int LLx, int LLy, int URx, int URy, int SC,int TR) {
		leftMotor = left;
		rightMotor = right;
		WRadius = radius;
		TRACK = track;
		tr = TR;
		llx = LLx;
		lly = LLy;
		urx = URx;
		ury = URy;
		sc = SC;
	
	}
	public static void run() {
		
		ColorDetector cd = new ColorDetector();
		Thread ColorThread = new Thread(cd);
		ColorThread.start();

		//Stop the motors
		leftMotor.stop();
		rightMotor.stop();
		leftMotor.setAcceleration(1000);
		rightMotor.setAcceleration(1000);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		
		
		//if (sc == 0) {
			int disX = urx - llx; // 6 - 3 = 3
			int disY = ury - lly; // 6 - 3 = 3
			int indexNum = (disX + 1) * (disY + 1); // 16
			destination = new int[indexNum][2];
			int flip = 0;
			/*
			for (int f = 2; f/2 - 1 <= disX ; f = f + 2) {
				if (flip % 2 == 0) {
				destination[f-2][0] = llx + f/2 - 1;
				//System.out.println(destination[f-2][0]);
				destination[f-2][1] = lly;
				//System.out.println(destination[f-2][0]);
				destination[f-1][0] = llx + f/2 - 1;
				destination[f-1][1] = ury;
				} else {
				destination[f-2][0] = llx + f/2 - 1;
				destination[f-2][1] = ury;
				destination[f-1][0] = llx + f/2 - 1;
				destination[f-1][1] = lly;	
				}
				flip++;
			}
			*/
			for (int line = 0; line <= disX; line++) { //disX = 7 - 3 = 4
				for (int column = 0; column <= disY; column++) { //disY = 7 - 3 = 4
					if (flip % 2 == 0) {
					destination[(disY + 1) * line + column][0] = llx + line;
					destination[(disY + 1) * line + column][1] = lly + column;
				} else {
					destination[(disY + 1) * line + column][0] = llx + line;
					destination[(disY + 1) * line + column][1] = lly + disY - column;
				}
				}
				flip++;
			}
			
			for (int i = 1; i < destination.length; i++) {
				Navigation.travelTo(destination[i][0], destination[i][1]);
			}
			
	//	} 
		/*
		else if (sc == 1) {
			
			
		} else if (sc == 2) {
			
			
		} else if (sc == 3) {	
			
			
		}
*/
	}
	
	
	public void avoid() {
		// Set this flag to prevent avoiding and navigating from occuring at the same time
		Navigation.isAvoiding = true;
		leftMotor.stop();
		rightMotor.stop();
		leftMotor.setAcceleration(1000);
		rightMotor.setAcceleration(1000);

		if (ColorDetector.colorId == tr) {
			Sound.beep();
			Navigation.travelTo(destination[destination.length - 2][0], destination[destination.length - 2][1]);
			System.exit(0);
		} else {
			Sound.beep();
			Sound.beep();
		}
		
		// turn 90 degrees clockwise then go forward
		double rightAngle = 90.0;
		double[] obstacleAvoidanceDistances = {23, 35};
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		leftMotor.rotate(convertAngle(WRadius, TRACK, rightAngle), true);
		rightMotor.rotate(-convertAngle(WRadius, TRACK, rightAngle), false);
		leftMotor.rotate(convertDistance(WRadius, obstacleAvoidanceDistances[0]), true);
		rightMotor.rotate(convertDistance(WRadius, obstacleAvoidanceDistances[0]), false);

		//Turn counterclockwise then go forward
		leftMotor.rotate(-convertAngle(WRadius, TRACK, rightAngle), true);
		rightMotor.rotate(convertAngle(WRadius, TRACK, rightAngle), false);
		leftMotor.rotate(convertDistance(WRadius, obstacleAvoidanceDistances[1]), true);
		rightMotor.rotate(convertDistance(WRadius, obstacleAvoidanceDistances[1]), false);
		
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		leftMotor.rotate(convertAngle(WRadius, TRACK, -rightAngle), true);
		rightMotor.rotate(-convertAngle(WRadius, TRACK, -rightAngle), false);
		leftMotor.rotate(convertDistance(WRadius, obstacleAvoidanceDistances[0]), true);
		rightMotor.rotate(convertDistance(WRadius, obstacleAvoidanceDistances[0]), false);

		//Turn counterclockwise then go forward
		leftMotor.rotate(-convertAngle(WRadius, TRACK, rightAngle), true);
		rightMotor.rotate(convertAngle(WRadius, TRACK, rightAngle), false);
		
		Navigation.isAvoiding = false;
		//Continue travelling to the rest of the desired coordinates
		for (int i = Navigation.currentDestinationIndex; i < destination.length - 1; i++) {
			Navigation.travelTo(destination[i][0], destination[i][1]);
		}
	}
	
	
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}


}
