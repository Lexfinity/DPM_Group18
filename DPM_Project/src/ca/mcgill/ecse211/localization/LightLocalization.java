package ca.mcgill.ecse211.localization;

import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.odometer.OdometerExceptions;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;


/**
 * 
 * Light Localization class
 * 
 * @author aarud
 *
 */
public class LightLocalization {

	
	private static final double LS_OFFSET = -13.6; //Distance from LS to center of rotation in cm. 
	private static final long CORRECTION_PERIOD = 1000;
	  private Navigation Navigator;
	  private EV3ColorSensor LS;
	  private Odometer odo;
	  public static double xToMove, yToMove;//variables that we need to get or need to have
		private static float color = 0;
		//private static final Port portColor = LocalEV3.get().getPort("S3");
		private static final int ROTATE_SPEED = 100;
		private static final double wheelRadius = 2.2;
		private static final double track = 11.3;
		private static final int FORWARDSPEED = 100;
		//private static SensorModes myColor = new EV3ColorSensor(LS);
		
		public LightLocalization(Navigation Navigator, EV3ColorSensor lightSensor) throws OdometerExceptions {
			this.Navigator = Navigator;
			this.LS = lightSensor;
			this.odo = Odometer.getOdometer();
		}

		public void lightLocalize() {
			//LS.getMode("Red")
			SampleProvider myColorSample = LS.getMode("Red");
			float[] sampleColor = new float[LS.sampleSize()];
			myColorSample.fetchSample(sampleColor, 0);
			color = sampleColor[0]*1000;
			
			double theta[] = new double [4];

			double a = 0;
			double b = 0;
			double r = 0;

			
			long correctionStart, correctionEnd;
			//rotateTheRobot(false, 90 , true);
			int count = 0;
			myColorSample.fetchSample(sampleColor, 0);
			color = sampleColor[0]*1000;
			rotateTheRobot(false, 90 , true);//first let it roll, 90 degree is sufficient for detecting the first black line
			while (true) {//while i did not detect enough data i.e enough theta
				correctionStart = System.currentTimeMillis();
				myColorSample.fetchSample(sampleColor, 0);
				color = sampleColor[0]*1000;
				rotateTheRobot(false, 90 , true);
				while(LS.getColorID() != 13)
				{
					
				}
				Sound.beep();//sound beep to let user know it have detect a rising edge
				stopMotor();
				theta[count] = odo.getXYT()[2];//get theta1
				count++;
				rotateTheRobot(false,10,false);//avoid detect same line twice
									
				// this ensure the odometry correction occurs only once every period
				correctionEnd = System.currentTimeMillis();
				if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
					try {
						Thread.sleep(CORRECTION_PERIOD - (correctionEnd - correctionStart));
					} catch (InterruptedException e) {
						// there is nothing to be done here
					}
				}

				if(count == 4) {
					//now compute a, b and r
					a = theta[2] - theta[0];
					b = theta[3] - theta[2] - theta[1];
					r = 360 - theta[3];

					rotateTheRobot(false, theta[3], false);//back to the direction of where it begins

					xToMove = -LS_OFFSET*Math.cos((a/2)*Math.PI/180);
					yToMove = LS_OFFSET*Math.cos((b/2)*Math.PI/180);

					advanceRobot(yToMove,false);
					rotateTheRobot(true, 90, false);//now change x position
					advanceRobot(xToMove,false);
					rotateTheRobot(false, 90, false);//back to the direction of where it begins
				}
			}
			
		}
		
		
		  /**
		   * this method is created to let the robot rotate certain angle
		   * @param cwOrCcw
		   * @param angleToRotate
		   */
		  private void rotateTheRobot(boolean cwOrCcw, double angleToRotate, boolean blocked) {
			  double absAngleToRotate = Math.abs(angleToRotate);
			  Navigation.leftMotor.setSpeed(ROTATE_SPEED);
			  Navigation.rightMotor.setSpeed(ROTATE_SPEED);
			  
			  //just a reminder that if want the robot to rotate clockwise then leftMotor rotate is positive and right is negative
			  //true means cw
			  //false means ccw
			  if(cwOrCcw) {
				  Navigation.leftMotor.rotate(convertAngle(wheelRadius, track, absAngleToRotate),true);
				  Navigation.rightMotor.rotate(-convertAngle(wheelRadius, track, absAngleToRotate),blocked);
			  }
			  else
			  {
				  Navigation.leftMotor.rotate(-convertAngle(wheelRadius, track, absAngleToRotate),true);
				  Navigation.rightMotor.rotate(convertAngle(wheelRadius, track, absAngleToRotate),blocked);
			  }
			  // TODO Auto-generated method stub
		  }
		  
		  /**
		   * This method allows the conversion of a distance to the total rotation of each wheel need to
		   * cover that distance.
		   * 
		   * @param radius
		   * @param distance
		   * @return
		   */
		  private static int convertDistance(double radius, double travelDistance) {
			  return (int) ((180.0 * travelDistance) / (Math.PI * radius));
		  }

		  private static int convertAngle(double radius, double width, double angle) {
			  return convertDistance(radius, Math.PI * width * angle / 360.0);
		  }  

		  /**
		   * this method is to stip the motor ,both of them, left and right
		   */
		  private void stopMotor() {
			  // TODO Auto-generated method stub
			  Navigator.leftMotor.setSpeed(0);
			  Navigator.rightMotor.setSpeed(0);	
		  }
		  
		  /**Advances the robot a desired amount of cm.
		   * 
		   * @param distanceToTravel Distance to travel in cm.
		   * @param instantReturn    True if the function is to be instantly returned. False if the function is to be returned after the travel is completed.
		   */
		   public void advanceRobot(double distanceToTravel, boolean instantReturn) {
		     
		     Navigator.leftMotor.setSpeed(FORWARDSPEED);
		     Navigator.rightMotor.setSpeed(FORWARDSPEED);
		              
		     Navigator.leftMotor.rotate(convertDistance(wheelRadius, distanceToTravel), true);
		     Navigator.rightMotor.rotate(convertDistance(wheelRadius, distanceToTravel), instantReturn);
		     
		   }


}
