package frc.robot;

import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.CANEncoder;



/**
 * Handles actions related to robot motion.
 * Includes motor controller, encoder, and gyro instances.
 * Created January 2018
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Evan McCoy
 * @author Brent Roberts
 */
public class DriveTrain extends DifferentialDrive {
	private static DriveTrain instance;

	public enum DTStates {
		TELEOP, //Normal driver control
		DIST_DRIVE, //A drive to a set distance
		TURN, //A turn to a degree
		DRIVE_TO_WALL_DIST, // A faster drive to a distance then switches to the final drive.
		DRIVE_TO_WALL_FINAL, //A drive designed to go slowly and end when it hits a wall.
		FIND_TARGET,
		TURN_TO_TARGET,
		HOLD;
	}
	
	private DTStates DTState = DTStates.TELEOP;

	public static double DRIVEACCEL = 0.05;

	public static final double TURNACCEL = .06;

	public static final double TURNMAX = 0.5, DRIVE_MAX = 1.0;
	
	private static final double HIGH_DISTANCE_CONVERSION_FACTOR = 2.02; /* was 222/106.180550*/// low appears to be 37.452019/(12*Math.PI);	
	/*
		Changed neos to public for testing, change back soon
	*/
	private static final CANSparkMax
			frontL = new CANSparkMax(Constants.DRIVE_FL, CANSparkMax.MotorType.kBrushless),
			frontR = new CANSparkMax(Constants.DRIVE_FR, CANSparkMax.MotorType.kBrushless),
			middleL = new CANSparkMax(Constants.DRIVE_ML, CANSparkMax.MotorType.kBrushless),
			middleR = new CANSparkMax(Constants.DRIVE_MR, CANSparkMax.MotorType.kBrushless),
			backL = new CANSparkMax(Constants.DRIVE_BL, CANSparkMax.MotorType.kBrushless),
			backR = new CANSparkMax(Constants.DRIVE_BR, CANSparkMax.MotorType.kBrushless); 
	private static final SpeedControllerGroup left = new SpeedControllerGroup(frontL, middleL, backL);
	private static final SpeedControllerGroup right = new SpeedControllerGroup(frontR, middleR, backR); 
	
	private double P = 0.009, I = 0, D = 0;
	private final double MAX_OUTPUT = 0.4, MIN_OUTPUT = 0.13;

	private double driveSpeed = 0, turnSpeed = 0, targetDrive = 0, targetTurn = 0;
	
	private CANEncoder encoderL, encoderR;
	//private double IPC_HIGH = 1, IPC_LOW = 1;
	private PID drivePID;
	public Heading heading;
	public Vision vis;
	//private Solenoid shifter;

	private boolean driveComp = true, visExit = true;
	private final double SLOW_VELOCITY = 500;
	private double targetDistance = 0;
	private final double DTW_SLOW_SPEED = 0.3, DTW_FAST_SPEED = 0.5, FINAL_DRIVE_DIST = 36; 
	
	/**
	 * Creates an instance of DriveTrain.
	 * Motor controller and encoder channels are determined in Constants.
	 */
	public DriveTrain() {
		super(left, right);
		
		initMotors();
		heading = new Heading();
		vis =  new Vision();
		encoderL = new CANEncoder(frontL);
		encoderR =  new CANEncoder(frontR);
		encoderL.setPositionConversionFactor(this.HIGH_DISTANCE_CONVERSION_FACTOR);
		encoderR.setPositionConversionFactor(this.HIGH_DISTANCE_CONVERSION_FACTOR);
		Common.dashNum("conversion factor", encoderL.getPositionConversionFactor());
		//shifter = new Solenoid(Constants.PCM_CAN_ID, Constants.Sol_SHIFTER);
		
		//pidL = new PID(0.005, 0, 0, false, true, "velL");
		//pidR = new PID(0.005, 0, 0, false, true, "velR");
		drivePID = new PID(P, I, D, true, false, "DrivePID", true);
		drivePID.setOutputLimits(-MAX_OUTPUT, MAX_OUTPUT);
		drivePID.setMinMagnitude(MIN_OUTPUT);

		
		instance = this;
	}

	/**
	 * A function that initiates the drivetrain.
	 */
	public void init() {
		drivePID.reset();
	}

	
	/**
	 * Function to init Motors
	 */
	private void initMotors() {
		frontL.restoreFactoryDefaults();
		frontR.restoreFactoryDefaults();
		middleL.restoreFactoryDefaults();
		middleR.restoreFactoryDefaults();
		backL.restoreFactoryDefaults();
		backR.restoreFactoryDefaults();

		frontL.setSmartCurrentLimit(40);
		frontR.setSmartCurrentLimit(40);
		middleL.setSmartCurrentLimit(40);
		middleR.setSmartCurrentLimit(40);
		backL.setSmartCurrentLimit(40);
		backR.setSmartCurrentLimit(40);
	}
	
	/**
	 * Shifts the drivetrain gearbox to high gear.
	 */
	/*public void shiftHigh() {
		shifter.set(false);
	}
	
	/**
	 * Shifts the drivetrain gearbox to low gear.
	 */
	/*public void shiftLow() {
		shifter.set(true);
	}
	
	/**
	 * Whether or not the drivetrain is in low gear.
	 * 
	 * @return - is low
	 */
	/*public boolean isShiftedLow() {
		return shifter.get();
	}
	
	/**
	 * Resets the counts of the left and right encoders.
	 */
	public void resetEncoders() {
		encoderL.setPosition(0);
		encoderR.setPosition(0);
		drivePID.reset();
	}
	
	
	/**
	 * Returns an instance of DriveTrain which is bound to the motor controllers.
	 * Only this instance will be functional.
	 * 
	 * @return the DriveTrain instance.
	 */
	public static DriveTrain instance() {
		return instance;
	}
	
	/**
	 * Get raw counts for the left encoder.
	 * 
	 * @return the counts
	 */
	/*public int getLeftCounts() {
		return encoderL.get();
	}*/
	
	/**
	 * Get the scaled distance of the left encoder.
	 * 
	 * @return the distance in inches
	 */
	public double getLeftDist() {
		return encoderL.getPosition();
	}
	
	/**
	 * Get the scaled velocity of the left encoder.
	 * 
	 * @return the velocity in inches/second
	 */
	public double getLeftVelocity() {
		return encoderL.getVelocity();
	}
	
	/**
	 * Get raw counts for the right encoder.
	 * 
	 * @return the counts
	 */
	/*public int getRightCounts() {
		return encoderR.get();
	}*/
	
	/**
	 * Get the scaled distance of the right encoder.
	 * 
	 * @return double - the distance in inches
	 */
	public double getRightDist() {
		return encoderR.getPosition();
	}
	
	/**
	 * Get the scaled velocity of the right encoder.
	 * 
	 * @return double - the velocity in inches/second
	 */
	public double getRightVelocity() {
		return encoderR.getVelocity();
	}
	
	/**
	 * Get the averaged counts between the two encoders.
	 * 
	 * @return int - the average counts
	 */
	/*public int getAverageCounts() {
		return (encoderL.get() + encoderR.get()) / 2;
	}*/
	
	/**
	 * Get the averaged scaled distance between the two encoders.
	 * Inverts the right side to get a robot average.
	 * 
	 * @return double - the average distance in inches
	 */
	public double getAverageDist() {
		return (this.getLeftDist() + -this.getRightDist()) / 2;
	}
	
	/**
	 * Get the averaged scaled velocity between the two encoders.
	 * Inverts the right side to get a robot average.
	 * 
	 * @return double - the average velocity in inches/second
	 */
	public double getAverageVelocity() {
		return (-this.getRightVelocity() + this.getLeftVelocity()) / 2;
	}
	
	/**
	 * An instance of Heading, a gyro utility and PID controller.
	 * 
	 * @return Heading - the heading instance.
	 */
	/*public Heading getHeading() {
		return this.heading;
	}*/
	
	/**
	 * Gets the drive acceleration value based on the elevator height and gear.
	 * 
	 * @return - the drive acceleration value
	 */
	/*public double getDriveAccel() {
		Elevator e = Robot.getElevator();
		double percentHeight = e.getInches() / e.ELEVATOR_HEIGHT;
		
		if (isShiftedLow()) {
			Common.dashStr("Gear", "Low");
			Common.dashNum("Calculated Acceleration", (1.0 - percentHeight) * (ACCEL_LG_LE - ACCEL_LG_HE) + ACCEL_LG_HE);
			return (1.0 - percentHeight) * (ACCEL_LG_LE - ACCEL_LG_HE) + ACCEL_LG_HE;
		}
		else {
			Common.dashStr("Gear", "High");
			Common.dashNum("Calculated Acceleration", (1.0 - percentHeight) * (ACCEL_HG_LE - ACCEL_HG_HE) + ACCEL_HG_HE);
			return (1.0 - percentHeight) * (ACCEL_HG_LE - ACCEL_HG_HE) + ACCEL_HG_HE;
		}
	}*/
	
	/**
	 * Gradually accelerate to a specified drive value.
	 * Uses DRIVEACCEL as chaneg value and DRIVE_MAX as max speed allowed.
	 * 
	 * @param target - the target drive value from -1 to 1
	 * @return double - the allowed drive value for this cycle.
	 */
	public double driveAccelCurve(double target) {

		if (Math.abs(target) > DRIVE_MAX) {
			if (target > 0 ) {
				target = DRIVE_MAX;
			} else {
				target = -DRIVE_MAX;
			}
		}

		//If the magnitude of current is greater than the minimum
		//If the difference is greater than the allowed acceleration
		if (Math.abs(driveSpeed - target) > DRIVEACCEL) {
			//Accelerate in the correct direction
            if (driveSpeed > target) {
                driveSpeed = driveSpeed - DRIVEACCEL;
            } else {
                driveSpeed = driveSpeed + DRIVEACCEL;
            }
		}
		

		//If the difference is less than the allowed acceleration, reach target
		else {
            driveSpeed = target;
        }
		return driveSpeed;
	 }
	 
	 /**
	  * Gradually accelerate to a specified turn value.
	  * 
	  * @param target - the target turn value from -1 to 1
	  * @return double - the allowed turn value at this cycle.
	  */
	 public double turnAccelCurve(double target) {
		 if (Math.abs(turnSpeed - target) > TURNACCEL) {
	    		if (turnSpeed > target) {
	    			turnSpeed = turnSpeed - TURNACCEL;
	    		} else {
	    			turnSpeed = turnSpeed + TURNACCEL;
	    		}
	    	} else {
	    		turnSpeed = target;
	    	}
		 if (turnSpeed >= 0) {
			 turnSpeed = Math.min(TURNMAX, turnSpeed);
		 } else {
			 turnSpeed = Math.max(-TURNMAX, turnSpeed);
		 }
	    return turnSpeed;
	}
	
	/**
	 * Arcade drive with an acceleration curve.
	 * 
	 * @param drive - the forward/backward value from -1 to 1.
	 * @param turn - the turn value from -1 to 1.
	 */
	public void teleopDrive(double drive, double turn) {
		DTState = DTStates.TELEOP;
		this.targetDrive = drive;
		this.targetTurn = turn;
	}
	
	
	/**
	 * Acceleration control for tank drive. Does not set motors.
	 * 
	 * @param left - the target left power.
	 * @param right - the target right power.
	 */
	/*public void accelTankDrive(double left, double right) {
		tankLeft = accelSide(tankLeft, left);
		tankRight = accelSide(tankRight, right);
		
		//System.out.println(tankLeft + ":" + tankRight);
	}*/
	
	/**
	 * Applies the current DriveTrain tankLeft and tankRight motor powers.
	 * Uses heading hold PID if heading hold is enabled with {@link Heading#setHeadingHold(boolean)}
	 */
	/*public void applyTankDrive() {
		if (heading.isHeadingHold()) {
			double turn = heading.turnRate() / 2;
			super.tankDrive(tankLeft + turn, tankRight - turn);
		}
		else {
			super.tankDrive(tankLeft, tankRight);
		}
	}*/
	
	/**
	 * A utility method that determines the change in current power, given a desired target and allowed power curve.
	 * 
	 * @param current the current motor power.
	 * @param target the target motor power.
	 * @return the new motor power.
	 */
	/*private double accelSide(double current, double target) {
		double TANKACCEL = getDriveAccel();
		//If the magnitude of current is less than the minimum
		if (Math.abs(current) < TANKMIN) {
			//Move to the lesser value of the minimum or the target, including desired direction.
			if (target > 0) {
				current = Math.min(TANKMIN, target);
			}
			else {
				current = Math.max(-TANKMIN, target);
			}
		}
		//If the magnitude of current is greater than the minimum
		//If the difference is greater than the allowed acceleration
		if (Math.abs(current - target) > TANKACCEL) {
			//Accelerate in the correct direction
            if (current > target) {
                current = current - TANKACCEL;
            } else {
                current = current + TANKACCEL;
            }
        }
		//If the difference is less than the allowed acceleration, reach target
		else {
            current = target;
        }
		return current;
	}*/

	/**
	 * Gets the drive train state.
	 * 
	 * @return The state the drive train is in.
	 */
	public DTStates getState() {
		return DTState;
	}
	
	/**
	 * Starts a distance drive.
	 * 
	 * @param distance The distance to drive in inches.
	 */
	public void driveDistance(double distance) {
		resetEncoders();
		drivePID.setTarget(distance);
		heading.setHeadingHold(true);
		DTState = DTStates.DIST_DRIVE;
		driveComp = false;
	}


	/**
	 * Moves the robot at a constant speed until it reaches the distance or has a slow velocity for 5 cycles
	 * 
	 * @param distance The distance to drive.
	 */
	public void driveToWall(double distance)  {
		resetEncoders();
		targetDistance = distance;
		Common.debug("Target distance: "+targetDistance);
		heading.setHeadingHold(true);
		driveComp = false;
		DTState = DTStates.DRIVE_TO_WALL_DIST;
	}

	/**
	 * Turns the robot to the target heading.
	 * 
	 * @param targetTurn The target heading from 360 to 0 to turn to.
	 */
	public void turn(double targetTurn) {
		heading.setHeading(targetTurn);
		DTState = DTStates.TURN;
		driveComp = false;
	}

	/**
	 * Enters FIND_TARGET and starts finding the target.
	 * 
	 * @param exit whether or no the robot will exit into TELOP every cycle.
	 */
	public void visionTrack(boolean exit) {
		driveComp = false;
		visExit = exit;
		DTState = DTStates.FIND_TARGET;
	}

	/**
	 * Sets the robot to hold state to hold it's position
	 */
	public void hold() {
		resetEncoders();
		heading.setHeadingHold(true);
		DTState = DTStates.HOLD;
		//driveComp = false; Maybe should be hear, doesn't seem like it.
	}

	/**
	 * Returns if the current drive is complete.
	 * 
	 * @return true if the current drive is complete.
	 */
	public boolean driveComplete() {
		return driveComp;
	}

	/**
	 * A function to run the drivetrain motors and update PID system.
	 */
	public void update() {
		double drive = 0, turn = 0; 
		switch(DTState) {
			case TELEOP:
				drive = driveAccelCurve(targetDrive);
				turn = -turnAccelCurve(targetTurn);
				break;
			case DIST_DRIVE:
				//set high gear
				driveComp = Math.abs(getAverageDist() - drivePID.getTarget()) <= 2.0;
				if (!driveComp) {
					drive = drivePID.calc(getAverageDist());
					turn = heading.turnRate();//turn rate was negative
    				Common.dashNum("drivePIDOUT", drive);
					Common.dashNum("TurnPIDOUT ", turn);					
				} else {
					hold();
				}
				break;
			case DRIVE_TO_WALL_DIST:
				//set high gear
				if (Math.abs(getAverageDist() - targetDistance) > FINAL_DRIVE_DIST) {
					if (targetDistance > 0) {
						drive = driveAccelCurve(DTW_FAST_SPEED);
					} else {
						drive = driveAccelCurve(-DTW_FAST_SPEED);
					}
					turn = heading.turnRate();
					
				} else {
					Common.debug("Completing DRIVE_TO_WALL_DIST: "+getAverageDist());
					if (targetDistance > 0) {
						drive = driveAccelCurve(DTW_SLOW_SPEED);
					} else {
						drive = driveAccelCurve(-DTW_SLOW_SPEED);
					}
					turn = heading.turnRate();
					DTState = DTStates.DRIVE_TO_WALL_FINAL;
				}
				break;
			case DRIVE_TO_WALL_FINAL:
				driveComp = getAverageVelocity() <= SLOW_VELOCITY;
				if (!driveComp) {
					if (targetDistance > 0) {
						drive = driveAccelCurve(DTW_SLOW_SPEED);
					} else {
						drive = driveAccelCurve(-DTW_SLOW_SPEED);
					}
					turn = heading.turnRate();
				} else {
					hold();
				}

				break;
			case TURN:
				driveComp = Math.abs(heading.getAngle() - heading.getTargetAngle()) <= 1.0; //accuracy was chosen randomly
				drive = 0;
				turn = heading.turnRate();
				if (driveComp) {
					DTState = DTStates.HOLD;
				}
				break;
			case FIND_TARGET:
				if (vis.ll.hasTarget()) {
					DTState = DTStates.TURN_TO_TARGET;
				} else {
					drive = driveAccelCurve(0); 
					turn = turnAccelCurve(-0.3); //Should be left
					if (visExit) {
						DTState = DTStates.TELEOP;
					}
				}
				break;
			case TURN_TO_TARGET:
				if (!vis.getAtTarget()) {
					drive =  vis.calcDrive();
					turn = vis.calcTurn();
					if (!vis.ll.hasTarget()) {
						DTState = DTStates.FIND_TARGET;
					}
				} else {
					hold();
				}
				break;
			case HOLD:
				drive = driveAccelCurve(0);
				turn = heading.turnRate();
				break;
		}
		Common.dashNum("Heading PID output", heading.turnRate());
		Common.dashNum("Angle", heading.getAngle());
		Common.dashNum("Drive output", drive);
		Common.dashNum("Turn output", turn);
		Common.dashNum("Average Distance", this.getAverageDist());
		Common.dashNum("Average velocity", getAverageVelocity());
		Common.dashStr("Drivetrain state", getState().toString());
		Common.dashBool("drive comp", driveComp);
		arcadeDrive(drive, turn);
		drivePID.update();
	}


}
