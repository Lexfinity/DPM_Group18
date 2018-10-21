package ca.mcgill.ecse211.lab4;

import ca.mcgill.ecse211.odometer.Odometer;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

/**
 * This class is meant as a skeleton for the colorDetector class to be used.
 * 
 * @author Zikun Lyu
 */
public class ColorDetector implements Runnable {
	
	  private static final Port lightPort = LocalEV3.get().getPort("S2");
	  private SensorModes lightSensor = new EV3ColorSensor(lightPort);
	  private SampleProvider colorSample = lightSensor.getMode("RGB");			// provides samples from this instance
	  private float[] colorvalues = new float[colorSample.sampleSize()];			// buffer in which data are returned
	  private final TextLCD lcd = LocalEV3.get().getTextLCD();
	  private static final double PERCENT_CHANGE = -35 ;
	  private static final double SENSOR_DISTANCE = 10.5;//15.75;
	  private static final double OFFSET = 0.7;
	  public static int colorId = 0;
	  
	  //private SampleProvider colorSample;
	  private double previousBrightness;
	  private double currentBrightness;
	  public static double distanceC = 0;
	  
		@Override
		public void run() {
			// TODO Auto-generated method stub
			 while (true){
					ColorDisplay();
				  }	
		}

	  private void ColorDisplay() {
		  // Trigger correction (When do I have information to correct?)
	      colorSample.fetchSample(colorvalues, 0);
	      currentBrightness = (colorvalues[0] + colorvalues[1] + colorvalues[2])*100;
	      
	      if(previousBrightness == -1) { //no set brightness
	    	  previousBrightness = currentBrightness;
	      }
	      
	      lcd.drawString("R: " +colorvalues[0]*100,0, 1);
	      lcd.drawString("G: " +colorvalues[1]*100,0, 2);
	      lcd.drawString("B: " +colorvalues[2]*100,0, 3);
	      lcd.drawString("Co: " +currentBrightness,0, 4);
	      lcd.drawString("Dis: " + distanceC,0, 5);
	      
	      float R = colorvalues[0];
	      float G = colorvalues[1];
	      float B = colorvalues[2];
	      
	      double Rn = R/Math.sqrt(Math.pow(R,2) + Math.pow(G,2) + Math.pow(B,2));
	      double Gn = G/Math.sqrt(Math.pow(R,2) + Math.pow(G,2) + Math.pow(B,2));
	      double Bn = B/Math.sqrt(Math.pow(R,2) + Math.pow(G,2) + Math.pow(B,2));
	      
	      if (currentBrightness > 17 && currentBrightness <= 22) {//blue
	    	  clearDisplay();
	    	  lcd.drawString("OBJECT DETECTED", 0, 5);
	    	  lcd.drawString("BLUE RING", 0, 6);
	    	  colorId = 1;
	      }
	      if (currentBrightness > 14 && currentBrightness <= 17) {//green
	    	  clearDisplay();
	    	  lcd.drawString("OBJECT DETECTED", 0, 5);
	    	  lcd.drawString("GREEN RING", 0, 6);
	    	  colorId = 2;
	      }
	      if (currentBrightness > 23) {//yellow
	    	  clearDisplay();
	    	  lcd.drawString("OBJECT DETECTED", 0, 5);
	    	  lcd.drawString("YELLOW RING", 0, 6);
	    	  colorId = 3;
	      }
	      if (currentBrightness > 12 && currentBrightness < 14) {//orange
	    	  clearDisplay();
	    	  lcd.drawString("OBJECT DETECTED", 0, 5);
	    	  lcd.drawString("ORANGE RING", 0, 6);
	    	  colorId = 4;
	      }
	      
	      if (currentBrightness <= 10) {
	    	  lcd.drawString("NOT DETECTED", 0, 6);
	    	  clearDisplay();
	    	  colorId = 0;
	      }

	      int buttonChoice;
			buttonChoice = Button.readButtons();
			if (buttonChoice == Button.ID_ESCAPE) {
				System.exit(0);
			}
	  }

	  public void clearDisplay() {
		  lcd.clear(5);
		  lcd.clear(6);
	  }

}
