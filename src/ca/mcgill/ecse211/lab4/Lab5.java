package ca.mcgill.ecse211.lab4;

import ca.mcgill.ecse211.odometer.*;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class Lab5 {

	private static final EV3LargeRegulatedMotor leftMotor =
			new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor =
			new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	private static final TextLCD lcd = LocalEV3.get().getTextLCD();
	private static final Port usPort = LocalEV3.get().getPort("S1");
	public static final double WHEEL_RAD = 2.2;
	public static final double TRACK = 17.98;
	private static int llx, lly, urx, ury, tr,sc;
	

	public static void main(String[] args) throws OdometerExceptions {

		int buttonChoice;
		@SuppressWarnings("resource") 
		SensorModes usSensor = new EV3UltrasonicSensor(usPort); 
		SampleProvider usDistance = usSensor.getMode("Distance"); 
		float[] usData = new float[usDistance.sampleSize()];
		
		// Odometer related objects
		final Odometer odometer = Odometer.getOdometer(leftMotor, rightMotor, TRACK, WHEEL_RAD);
		Display display = new Display(lcd);
		UltrasonicPoller ultrasonicP = new UltrasonicPoller(usDistance, usData);

		do {
			lcd.clear();
			lcd.drawString("press middle button", 0, 0);
			buttonChoice = Button.waitForAnyPress(); // press middle button to start
		} while (buttonChoice != Button.ID_ENTER);

		//Start threads
		if (buttonChoice == Button.ID_ENTER) {
			lcd.clear();
			Thread odometerThread = new Thread(odometer);
			odometerThread.start();
			Thread odoDisplayThread = new Thread(display);
			odoDisplayThread.start();
			Thread UltrasonicThread = new Thread(ultrasonicP);
			UltrasonicThread.start();

			(new Thread() {
				public void run() {
					UltrasonicLocalizer.run(leftMotor, rightMotor, WHEEL_RAD, TRACK, odometer);
				}
			}).start();

			while (Button.waitForAnyPress() != Button.ID_ENTER); 
			while(true) {										
				if (UltrasonicLocalizer.status = true) {		
					(new Thread() {	   
						public void run() {
							sc = 0;
							LightLocalizer.run(leftMotor, rightMotor, WHEEL_RAD, TRACK, odometer,sc);
						}
					}).start();
					break;
				}
			}
		}
		/*
		lcd.clear();
		lcd.drawString("This Lab is finished", 0, 0);
		*/	
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
}
