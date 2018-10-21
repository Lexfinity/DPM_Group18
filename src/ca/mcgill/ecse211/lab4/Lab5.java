package ca.mcgill.ecse211.lab4;

import ca.mcgill.ecse211.odometer.*;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

/**
 * This class is meant as a skeleton for the Lab5 class to be used.
 * 
 * @author Zikun Lyu
 */
public class Lab5 {
	private static final EV3LargeRegulatedMotor leftMotor =
			new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor =
			new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	private static final TextLCD lcd = LocalEV3.get().getTextLCD();
	private static final Port usPort = LocalEV3.get().getPort("S1");
	public static final double WHEEL_RAD = 2.2;
	public static final double TRACK = 11.7;
	private static int llx, lly, urx, ury; // range from 0 to 8
	private static int tr,sc;
	public static final int[] searchRegion = {3,3,7,7};
	public static final double TILE_SIZE = 30.48;
	public static UltrasonicPoller usPoller;
	public static boolean navigationFinish = false;
	
	

	public static void main(String[] args) throws OdometerExceptions {

		int buttonChoice;
		@SuppressWarnings("resource") 
		SensorModes usSensor = new EV3UltrasonicSensor(usPort); 
		SampleProvider usDistance = usSensor.getMode("Distance"); 
		float[] usData = new float[usDistance.sampleSize()];
		
		// Odometer related objects
		final Odometer odometer = Odometer.getOdometer(leftMotor, rightMotor, TRACK, WHEEL_RAD);
		Display display = new Display(lcd);
		//UltrasonicPoller ultrasonicP = new UltrasonicPoller(usDistance, usData);
		
		llx = searchRegion[0];
		lly = searchRegion[1];
		urx = searchRegion[2];
		ury = searchRegion[3];
		sc = 0;
		tr = 1; // range from 1 to 4
		
		do {
			lcd.clear();
			lcd.drawString("press middle button", 0, 0);
			buttonChoice = Button.waitForAnyPress(); // press middle button to start
		} while (buttonChoice != Button.ID_ENTER);
		

		//Start threads
		if (buttonChoice == Button.ID_ENTER) {
			lcd.clear();
			/*
			Thread odometerThread = new Thread(odometer);
			odometerThread.start();
			Thread odoDisplayThread = new Thread(display);
			odoDisplayThread.start();
			Thread UltrasonicThread = new Thread(ultrasonicP);
			UltrasonicThread.start();
			*/

			(new Thread() {
				public void run() {
					UltrasonicLocalizer.run(leftMotor, rightMotor, WHEEL_RAD, TRACK, odometer);
				}
			}).start();

			while (Button.waitForAnyPress() != Button.ID_ENTER); 
			while(true) {										
				if (UltrasonicLocalizer.ultrasonicLocalizerFinish = true) {		
					(new Thread() {	   
						public void run() {
							LightLocalizer.run(leftMotor, rightMotor, WHEEL_RAD, TRACK, odometer);
						}
					}).start();
					break;
				}
			}

			while (Button.waitForAnyPress() != Button.ID_ENTER); 
			while(true) {										
				if (LightLocalizer.lightLocalizerFinish = true) {	
					switch (sc) {
					case 0: 
						odometer.setXYT(TILE_SIZE,TILE_SIZE, 0);
						break;
					case 1:
						odometer.setXYT(7 * TILE_SIZE,TILE_SIZE, 270);
						break;
					case 2:
						odometer.setXYT(7 * TILE_SIZE, 7 * TILE_SIZE, 180);
						break;
					case 3:
						odometer.setXYT(TILE_SIZE, 7 * TILE_SIZE, 90);
					}
					Navigation.run(leftMotor, rightMotor, WHEEL_RAD, TRACK, odometer, llx, lly, urx, ury, sc);
					navigationFinish = true;
						}
					break;
					}
			
			
			while (Button.waitForAnyPress() != Button.ID_ENTER); 
			(new Thread() {
				public void run() {
					MoveFollowingLine.run();
				}
			}).start(); 
			
			}
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
}
