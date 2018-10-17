package ca.mcgill.ecse211.lab4;

import ca.mcgill.ecse211.odometer.Odometer;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class LightLocalizer {
	private static final Port portColor = LocalEV3.get().getPort("S4"); 
	private static SensorModes myColor = new EV3ColorSensor(portColor);
	private static Odometer odometer;
	private static EV3LargeRegulatedMotor leftMotor;
	private static EV3LargeRegulatedMotor rightMotor;
	private static double TRACK;
	private static double WRadius;
	private static final int ROTATE_SPEED = 120;
	private static float color = 0;
	private static final double dis = 12.5; 
	
	public static void run(EV3LargeRegulatedMotor left, EV3LargeRegulatedMotor right,
			double radius, double track, Odometer odom) {
		leftMotor = left;
		rightMotor = right;
		WRadius = radius;
		TRACK = track;
		
		@SuppressWarnings("resource")
		SampleProvider myColorSample = myColor.getMode("Red");
		float[] sampleColor = new float[myColor.sampleSize()];
		
		//Stop the motors
		left.stop();
		right.stop();
		left.setAcceleration(5000);
		right.setAcceleration(5000);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		
		//Rotating to a heading of 45 degrees
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		leftMotor.rotate(convertAngle(WRadius, TRACK, 45), true);
		rightMotor.rotate(-convertAngle(WRadius, TRACK, 45), false);

		//Go forward
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		leftMotor.forward();
		rightMotor.forward();

		//Use the light sensor to detect lines
		while (true) {
			myColorSample.fetchSample(sampleColor, 0); 
			color = sampleColor[0] * 1000; 

			//When a line is detected, go backwards for a distance equal to the distance between the light sensor and the center of the robot
			if ( color < 450 ) { 
				Sound.beep();
				leftMotor.setSpeed(ROTATE_SPEED);
				rightMotor.setSpeed(ROTATE_SPEED);
				leftMotor.rotate(convertDistance(WRadius, -dis), true);  
				rightMotor.rotate(convertDistance(WRadius, -dis), false);
				
				leftMotor.setSpeed(0); 
				rightMotor.setSpeed(0);
				
				break;
			}

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
		//Rotate to face the true north
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		leftMotor.rotate(convertAngle(WRadius, TRACK, -45), true);
		rightMotor.rotate(-convertAngle(WRadius, TRACK, -45), false);
	}
	
	
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

}
