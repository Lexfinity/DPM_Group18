package ca.mcgill.ecse211.ring_capture;

import java.util.Arrays;

import lejos.hardware.Sound;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import ca.mcgill.ecse211.odometer.*;
/**
 * ColorDectection class
 * 
 * @author aarud
 *
 */

public class ColorDetection implements Runnable {
	
	private EV3ColorSensor cSensor;
	private float[] colorSample;
	private float[] usData;
	private SensorModes usSensor;
	private float distance;
	private TextLCD t;
	static EV3LargeRegulatedMotor leftMotor;
	static EV3LargeRegulatedMotor rightMotor;
	private static EV3LargeRegulatedMotor sensorMotor;
	
	private static final int BACKWARD_SPEED = 150;
	private static final int ROTATE_SPEED = 150;
	private static final double TILE_SIZE = 30.48;
	private static final double TRACK = 18.5;
	private static final double RADIUS = 2.2;

	//average NORMALIZED values of RGB for each ring. Data was collected by the test engineer.
	//all color data to be used in the class are NORMALIZED
	private double mBlueR = 0.1732410055;
	private double mBlueG = 0.6778531281;
	private double mBlueB = 0.7144947101;
	private double mGreenR = 0.4777487339;
	private double mGreenG = 0.8592604804;
	private double mGreenB = 0.1828320925;
	private double mYellowR = 0.8541708187;
	private double mYellowG = 0.5005476676;
	private double mYellowB = 0.140869603;
	private double mOrangeR = 0.9547663589;
	private double mOrangeG = 0.2766071505;
	private double mOrangeB = 0.1091314998;
	private double mEmptyR =  0.1345000000;
	private double mEmptyB =  0.0855000000;
	private double mEmptyG =  0.0122500000;

	private float R, G, B;
	private double nR, nG, nB;
	private double dBlue, dGreen, dYellow, dOrange, dEmpty;
	
	/*
	 * Calculations explanation:
	 * 1- Calculate mean normalized RGB data for each ring
	 * 2- Collect RGB color sample using the sample.
	 * 3- Normalize the collected sample.
	 * 4- Calculate the deviation from the mean data for each ring.
	 * 5- The ring with the smallest deviation is the found color.
	 */

	public ColorDetection(EV3ColorSensor cSensor, SensorModes usSensor, float[] usData, TextLCD t, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, EV3LargeRegulatedMotor sensorMotor) {
		this.cSensor = cSensor;
		this.usSensor = usSensor;
		this.usData = usData;
		this.t = t;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.sensorMotor = sensorMotor;
	}
	
public static void rotateToScan() {
	
	leftMotor.setSpeed(ROTATE_SPEED);
	rightMotor.setSpeed(ROTATE_SPEED);
	
	leftMotor.backward();
	rightMotor.backward();
	leftMotor.rotate(-convertDistance(RADIUS,  TILE_SIZE/2), true);
    rightMotor.rotate(-convertDistance(RADIUS, TILE_SIZE/2), false);
    
    leftMotor.setSpeed(ROTATE_SPEED);
	rightMotor.setSpeed(ROTATE_SPEED);

	leftMotor.rotate(convertAngle(RADIUS, TRACK, 180.0), true);
    rightMotor.rotate(-convertAngle(RADIUS, TRACK, 180.0), false);
    
    leftMotor.setSpeed(ROTATE_SPEED/2);
	rightMotor.setSpeed(ROTATE_SPEED/2);
    
    leftMotor.rotate(-convertDistance(RADIUS,  TILE_SIZE/3.5), true);
    rightMotor.rotate(-convertDistance(RADIUS, TILE_SIZE/3.5), false);
    

}

public static void rotateToGrab() {

	leftMotor.setSpeed(ROTATE_SPEED);
	rightMotor.setSpeed(ROTATE_SPEED);
	
	leftMotor.forward();
	rightMotor.forward();
	leftMotor.rotate(convertDistance(RADIUS,  TILE_SIZE/2), true);
    rightMotor.rotate(convertDistance(RADIUS, TILE_SIZE/2), false);
    
    leftMotor.setSpeed(ROTATE_SPEED);
	rightMotor.setSpeed(ROTATE_SPEED);

	leftMotor.rotate(convertAngle(RADIUS, TRACK, 180.0), true);
    rightMotor.rotate(-convertAngle(RADIUS, TRACK, 180.0), false);
    
    leftMotor.setSpeed(ROTATE_SPEED/2);
	rightMotor.setSpeed(ROTATE_SPEED/2);
    
    leftMotor.rotate(convertDistance(RADIUS,  TILE_SIZE ), true);
    rightMotor.rotate(convertDistance(RADIUS, TILE_SIZE  ), false);

	
}

public static void rotateSensor() {
	
	sensorMotor.rotate(-180);
	
}


private static int convertDistance(double radius, double distance) {
    return (int) ((180.0 * distance) / (Math.PI * radius));
  }

  private static int convertAngle(double radius, double width, double angle) {
    return convertDistance(radius, Math.PI * width * angle / 360.0);
  }
  
  public void detectRing() {
		cSensor.setFloodlight(true);
		SampleProvider csColors = cSensor.getRGBMode();
		colorSample = new float[csColors.sampleSize()];

		while(true) {

			usSensor.fetchSample(usData, 0);
			distance = usData[0] * 100;
			String display = "";
			
			
			//detect object if it is within 15cm,
			//and begins to position itself for ring scanning 
			
			if(distance < 15) {
				t.clear();
				
				rotateToScan();
				
				t.drawString("Object Detected", 0, 0);
				t.drawString("Distance = " + distance, 0, 2);
				csColors.fetchSample(colorSample, 0);
				
				R = colorSample[0];
				G = colorSample[1];
				B = colorSample[2];

				//normalize
				nR = R/(Math.sqrt(R*R + G*G + B*B));
				nG = G/(Math.sqrt(R*R + G*G + B*B));
				nB = B/(Math.sqrt(R*R + G*G + B*B));

				//calculate deviation for each ring
				dBlue = Math.sqrt(Math.pow(nR - mBlueR, 2) + Math.pow(nG - mBlueG, 2) + Math.pow(nB - mBlueB, 2));
				dGreen = Math.sqrt(Math.pow(nR - mGreenR, 2) + Math.pow(nG - mGreenG, 2) + Math.pow(nB - mGreenB, 2));
				dYellow = Math.sqrt(Math.pow(nR - mYellowR, 2) + Math.pow(nG - mYellowG, 2) + Math.pow(nB - mYellowB, 2));
				dOrange = Math.sqrt(Math.pow(nR - mOrangeR, 2) + Math.pow(nG - mOrangeG, 2) + Math.pow(nB - mOrangeB, 2));
				dEmpty = Math.sqrt(Math.pow(nR - mEmptyR, 2) + Math.pow(nG - mEmptyG, 2) + Math.pow(nB - mEmptyB, 2));

				double[] list = {dBlue, dGreen, dYellow, dOrange, dEmpty};

				//sorted array
				Arrays.sort(list);

				//print list[0] which is going to the detected color
				if(list[0] == dBlue) {
					display = "Blue";
					Sound.beep();
//					break;
				}

				if(list[0] == dGreen) {
					display = "Green";
					Sound.beep();
					Sound.beep();
//					break;
				}

				if(list[0] == dYellow) {
					display = "Yellow";
					Sound.beep();
					Sound.beep();
					Sound.beep();
//					break;
				}

				if(list[0] == dOrange) {
					display = "Orange";
					Sound.beep();
					Sound.beep();
					Sound.beep();
					Sound.beep();
//					break;
				}
				
				if(list[0] == dEmpty) {
					display = "Empty";
					Sound.buzz();
					rotateSensor();
				}
				
				t.drawString(display, 0, 3);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				t.clear();
				
				rotateToGrab();
				break;

				
			}
			
		}
  }

	@Override
	public void run() {
		detectRing();


	}
}
	

	
	
//	/**
//	 * Constructor for ColorDectection 
//	 * 
//	 * @param r
//	 * @param g
//	 * @param b
//	 * @param currentBrightness
//	 */
//	
//	public ColorDetection(double r, double g, double b, double currentBrightness) {
//		
//	}
//	
//	
//	
//
//}


/*
colorMotor.rotate(0);
while(true) {
		csColors.fetchSample(colorSample, 0);
	R = colorSample[0] * 1000;
	G = colorSample[1] * 1000;
	B = colorSample[2] * 1000;
	Sound.beep();
	break;
}

	//Detecting Color while sensor is slightly to the right position
	colorMotor.rotate(90);
	while(true) {
	csColors.fetchSample(colorSample, 0);
	R = colorSample[0] * 1000;
	G = colorSample[1] * 1000;
	B = colorSample[2] * 1000;
	Sound.beep();
	break;
	}
//Detecting Color while sensor is at the bottom position
	colorMotor.rotate(90);
	while (true) {
	csColors.fetchSample(colorSample, 0);
	R = colorSample[0] * 1000;
	G = colorSample[1] * 1000;
	B = colorSample[2] * 1000;
	Sound.beep();
	break;
	}
*/