
package ca.mcgill.ecse211.lab4;

import lejos.robotics.SampleProvider;

public class UltrasonicPoller extends Thread {
	private SampleProvider us;
	private float[] usData;
	private static int distance;
	public MoveFollowingLine move;
	

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
			if (Lab5.navigationFinish) {
				//MoveFollowingLine.distance = distance;
				ColorDetector.distanceC = distance;
				 if (distance <= 3 && !Navigation.isAvoiding && !MoveFollowingLine.isTurning) {
		        	  move.avoid();
		          }
			}
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