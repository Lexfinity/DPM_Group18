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
	  private UltrasonicController cont;
	  private float[] usData;

	  public SensorPoller(SampleProvider us, float[] usData, UltrasonicController cont) {
	    this.us = us;
	    this.cont = cont;
	    this.usData = usData;
	  }
	

}
