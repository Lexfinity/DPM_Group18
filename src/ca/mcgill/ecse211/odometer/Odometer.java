package ca.mcgill.ecse211.odometer;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Odometer extends OdometerData implements Runnable {

  private OdometerData odoData;
  private static Odometer odo = null; // Returned as singleton

  // Motors and related variables
  private int leftMotorTachoCount; 
  private int rightMotorTachoCount; 
  private int leftMotorLastTacho; 
  private int rightMotorLastTacho; 
  private EV3LargeRegulatedMotor leftMotor;
  private EV3LargeRegulatedMotor rightMotor;

  private final double TRACK;
  private final double WHEEL_RAD;



  private static final long ODOMETER_PERIOD = 10; 

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
  public void run() {
    double distL,distR,deltaD,dX,dY,dT;//
    double deltaT;
    deltaT = odo.getXYT()[2];
    leftMotor.resetTachoCount();
    rightMotor.resetTachoCount();
    leftMotorLastTacho = leftMotor.getTachoCount();
    rightMotorLastTacho = rightMotor.getTachoCount();

    while (true) {
      leftMotorTachoCount = leftMotor.getTachoCount(); 
      rightMotorTachoCount = rightMotor.getTachoCount();

      distL =  Math.PI * this.WHEEL_RAD * (leftMotorTachoCount - leftMotorLastTacho) / 180; 
      distR =  Math.PI * this.WHEEL_RAD * (rightMotorTachoCount - rightMotorLastTacho) / 180;
      leftMotorLastTacho = leftMotorTachoCount;                                     
      rightMotorLastTacho = rightMotorTachoCount;
      deltaD = 0.5 * (distL + distR);                                                 
      dT = ((distL - distR) / this.TRACK) * 360 / (2 *  Math.PI);                           
      deltaT += dT;                                                                       
      dX = deltaD * Math.sin(deltaT * 2 *  Math.PI / 360);                                   
      dY = deltaD * Math.cos(deltaT * 2 *  Math.PI / 360);                                     
      odo.update(dX,dY,dT);                                                           

      try {
        Thread.sleep(ODOMETER_PERIOD);
      } catch (InterruptedException e) {
        // there is nothing to be done
      }
    }
  }

}
