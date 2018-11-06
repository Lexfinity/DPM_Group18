package ca.mcgill.ecse211.odometer;


import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.odometer.OdometerData;
import lejos.hardware.motor.EV3LargeRegulatedMotor;


/**
 * Odometer class 
 * 
 * @author aarud
 *
 */
public class Odometer extends OdometerData implements Runnable {

	  private OdometerData odoData;
	  private static Odometer odo = null; // Returned as singleton

	  // Motors and related variables
	  private int leftMotorTachoCount;
	  private int rightMotorTachoCount;
	  private EV3LargeRegulatedMotor leftMotor;
	  private EV3LargeRegulatedMotor rightMotor;

	  private final double TRACK;
	  private final double WHEEL_RAD;

	  //private double[] position;
	 
	  private double X = 0, Y = 0, Theta = 0;
	  private double lastTachoR, lastTachoL;
	  private double deltaT, deltaD, dX, dY, distanceL, distanceR; 


	  private static final long ODOMETER_PERIOD = 25; // odometer update period in ms

	  /**
	   * This is the default constructor of this class. It initiates all motors and variables once.It
	   * cannot be accessed externally.
	   * 
	   * @param leftMotor
	   * @param rightMotor
	   * @throws OdometerExceptions
	   */
	  private Odometer(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
	      final double TRACK, final double WHEEL_RAD) throws OdometerExceptions {
	    odoData = OdometerData.getOdometerData(); // Allows access to x,y,z
	                                              // manipulation methods
	    this.leftMotor = leftMotor;
	    this.rightMotor = rightMotor;

	    // Reset the values of x, y and z to 0
	    odoData.setXYT(0, 0, 0);

	    this.leftMotorTachoCount = 0;
	    this.rightMotorTachoCount = 0;

	    this.TRACK = TRACK;
	    this.WHEEL_RAD = WHEEL_RAD;

	  }

	  /**
	   * This method is meant to ensure only one instance of the odometer is used throughout the code.
	   * 
	   * @param leftMotor
	   * @param rightMotor
	   * @return new or existing Odometer Object
	   * @throws OdometerExceptions
	   */
	  public synchronized static Odometer getOdometer(EV3LargeRegulatedMotor leftMotor,
	      EV3LargeRegulatedMotor rightMotor, final double TRACK, final double WHEEL_RAD)
	      throws OdometerExceptions {
	    if (odo != null) { // Return existing object
	      return odo;
	    } else { // create object and return it
	      odo = new Odometer(leftMotor, rightMotor, TRACK, WHEEL_RAD);
	      return odo;
	    }
	  }

	  /**
	   * This class is meant to return the existing Odometer Object. It is meant to be used only if an
	   * odometer object has been created
	   * 
	   * @return error if no previous odometer exists
	   */
	  public synchronized static Odometer getOdometer() throws OdometerExceptions {

	    if (odo == null) {
	      throw new OdometerExceptions("No previous Odometer exits.");

	    }
	    return odo;
	  }

	  /**
	   * This method is where the logic for the odometer will run. Use the methods provided from the
	   * OdometerData class to implement the odometer.
	   */
	  // run method (required for Thread)
	  public void run() {
	    long updateStart, updateEnd;

	    while (true) {
	      updateStart = System.currentTimeMillis();

	      leftMotorTachoCount = leftMotor.getTachoCount();
	      rightMotorTachoCount = rightMotor.getTachoCount();

	      // TODO Calculate new robot position based on tachometer counts
	     
	      //Calculates the distance of each wheel based on the cirrcumfrence and number of revolutions 
	      
	       distanceL = Math.PI*WHEEL_RAD*(leftMotorTachoCount - lastTachoL)/180;
	       distanceR = Math.PI*WHEEL_RAD*(rightMotorTachoCount - lastTachoR)/180;
	       
	       
	       //updates the current tacho count as the last tacho count
	       
	       lastTachoL= leftMotorTachoCount;
	       lastTachoR= rightMotorTachoCount;
	      
	       //DeltaD gives the displacement value of the robot
	       
	       deltaD = 0.5*(distanceL + distanceR);
	       
	       //DeltaT gives teh change in Theta 
	       deltaT = (distanceL - distanceR)/ TRACK;
	       Theta += Math.toDegrees(deltaT);
	       
	       //Calculates the difference in X and Y position
	       dX = deltaD*Math.sin(Math.toRadians(Theta));
	       dY = deltaD*Math.cos(Math.toRadians(Theta));
	       
	       //Updates the X and Y position values
	       X += dX;
	       Y += dY;
	       
	      // TODO Update odometer values with new calculated values
	      odo.update(dX, dY,Math.toDegrees(deltaT));
	      
	      

	      // this ensures that the odometer only runs once every period
	      updateEnd = System.currentTimeMillis();
	      if (updateEnd - updateStart < ODOMETER_PERIOD) {
	        try {
	          Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
	        } catch (InterruptedException e) {
	          // there is nothing to be done
	        }
	      }
	    }
	  }
	
	

}
