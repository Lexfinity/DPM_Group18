package ca.mcgill.ecse211.project;

import ca.mcgill.ecse211.ring_capture.ArmController;
import ca.mcgill.ecse211.ring_capture.ColorDetection;
import ca.mcgill.ecse211.project.Display;
import ca.mcgill.ecse211.localization.LightLocalization;
import ca.mcgill.ecse211.localization.UltrasonicLocalization;
import ca.mcgill.ecse211.project.SensorPoller;
import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.odometer.OdometerExceptions;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class GameController {
	
	private static final EV3LargeRegulatedMotor leftMotor =
			new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor =
			new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	private static final EV3MediumRegulatedMotor armMotor =
			new EV3MediumRegulatedMotor(LocalEV3.get().getPort("B"));
	private static final TextLCD lcd = LocalEV3.get().getTextLCD();
	private static final Port usPort = LocalEV3.get().getPort("S1");
	public static final double WHEEL_RAD = 2.2;
	public static final double TRACK = 15.5;
	private static int llx, lly, urx, ury; // range from 0 to 8
	private static int tr,sc;
	public static final int[] searchRegion = {3,3,7,7};
	public static final double TILE_SIZE = 30.48;
	public static SensorPoller usPoller;
	
	/**
	 * GameController class houses the main method, from this class
	 * all the other methods are called upon during the game. 
	 * @param args
	 * @throws OdometerExceptions 
	 */

	public static void main(String[] args) throws OdometerExceptions {
		// TODO Auto-generated method stub
		
		int buttonChoice;
		@SuppressWarnings("resource") 
		SensorModes usSensor = new EV3UltrasonicSensor(usPort); 
		SampleProvider usDistance = usSensor.getMode("Distance"); 
		float[] usData = new float[usDistance.sampleSize()];
		

		//Odometer odometer = Odometer.getOdometer(leftMotor, rightMotor, TRACK, WHEEL_RAD);
		Odometer odometer = Odometer.getOdometer(leftMotor, rightMotor, TRACK, WHEEL_RAD);
		Display display = new Display(lcd);
		SensorPoller usPoller = new SensorPoller(usDistance, usData);
		UltrasonicLocalization url = new UltrasonicLocalization();
		//LightLocalization ll = new LightLocalization();
		ArmController ac = new ArmController(armMotor);

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

			Thread UltrasonicThread = new Thread(usPoller);
			UltrasonicThread.start();
			
			ac.grab();
			//url.run(leftMotor, rightMotor, WHEEL_RAD, TRACK, odometer);
			while (Button.waitForAnyPress() != Button.ID_LEFT); 
			ac.unload();
			
			//ll.run(leftMotor, rightMotor, WHEEL_RAD, TRACK, odometer);
		}

	}

}
