package ca.mcgill.ecse211.ring_capture;

import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;

/**
 * 
 * @author aarud
 *
 */

public class ArmController {
	private static EV3MediumRegulatedMotor armMotor;
	
	
	
	/**
	 * Constructor for ArmController object
	 * 
	 * @param armMotor
	 */
	public ArmController(EV3MediumRegulatedMotor arm) {
		this.armMotor = arm;
		
		
	}
	
	/**
	 * grab method to contract the mechanical 
	 * arm of the robot to acquire ring
	 * 
	 */
	public static void grab() {
		armMotor.rotate(75);
		
	}
	
	/**
	 * unload method to expand the mechanical
	 * arm to release the acquired ring
	 * 
	 */
	public static void unload() {
		armMotor.rotate(-80);
		Sound.beep();
		Sound.beep();
		Sound.beep();
		Sound.beep();
		Sound.beep();
		
	}

}
