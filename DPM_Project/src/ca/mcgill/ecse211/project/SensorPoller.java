package ca.mcgill.ecse211.project;

import ca.mcgill.ecse211.project.UltrasonicController;
import lejos.robotics.SampleProvider;


/**
 * SensorPoller class
 * @author aarud
 *
 */
public class SensorPoller extends Thread {
	private SampleProvider us;
	private static int distance;
	//private UltrasonicController cont;
	private float[] usData;

	  public SensorPoller(SampleProvider us, float[] usData) {
	    this.us = us;
	   // this.cont = cont;
	    this.usData = usData;
	  }
	  
	  public static int getDistance() {
			return distance;
		}
	
	  
	  public void run() {
		    int distance;
		    while (true) {
		      us.fetchSample(usData, 0); // acquire data
		      distance = (int) (usData[0] * 100.0); // extract from buffer, cast to int
		      //cont.processUSData(distance); // now take action depending on value
		      try {
		        Thread.sleep(20);
		      } catch (Exception e) {
		      } // Poor man's timed sampling
		    }
		  }


}
