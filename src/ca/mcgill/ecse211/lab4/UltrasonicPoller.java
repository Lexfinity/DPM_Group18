
package ca.mcgill.ecse211.lab4;

import lejos.robotics.SampleProvider;

public class UltrasonicPoller extends Thread {
	private SampleProvider us;
	private float[] usData;
	private static int distance;

	public UltrasonicPoller(SampleProvider us, float[] usData) {
		this.us = us;
		this.usData = usData;
	}

	/*
	 * Sensors now return floats using a uniform protocol. Need to convert US result to an integer
	 * [0,255] (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		while (true) {
			us.fetchSample(usData, 0);
			distance = (int) (usData[0] * 100.0); 
			if (distance > 255) distance = 255; 
			try {
				Thread.sleep(10);
			} catch (Exception e) {
			}
		}
	}

	public static int getDistance() {
		return distance;
	}
}