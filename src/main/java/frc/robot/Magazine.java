package frc.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Talon;
import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkMax;

/**
 * The Magazine sub-system consists of a motor and two ir make/break sensors to manage ball handling.
 * The bottom IR sensor on the Intake side of the magazine will detect when a ball is feed in from the intake.
 * The magazine motor will run to load the ball into the magazine until it is past the sensor.
 * The top IR sensor on Flywheel side of the magazine is used to detect when the magazine is at capacity, as well
 * as detect individual balls being feed into the flywheel, so that a ball is thrown only when the Flywheel is ready
 * (upto rpm).  This also provides a way to shoot individual balls as well stream balls (full automatic), as soon as
 * the flywheel is ready.
 *
 * The magazine can hold upto 5 balls, when loaded tightly together.  It is possible to have less than 5 balls
 * in the magazine and trigger the top sensor.
 * State logic controls the coordination of the motors, sensors and interaction with the Flywheel.  See state logic 
 * for more details.
 *
 * Magazine must be instantiated by the Shooter object, which will provide acces to the Flywheel instance, for status 
 * checking.
 *
 * Run init() when starting the robot and then call update() every robot cycle to drive the state process.
 */

public class Magazine {

	//private static final Spark magMot = new Spark(Constants.PWM_MAGAZINE_MOTOR);
	private static final Talon magMot = new Talon(Constants.PWM_MAGAZINE_MOTOR);
	private DigitalInput topBeamBreak = new DigitalInput(Constants.DIO_TOP_BEAMBREAK);
	private DigitalInput bottomBeamBreak = new DigitalInput(Constants.DIO_BOTTOM_BEAMBREAK);
	private Timer timer = new Timer();
	private Timer jamTimer =  new Timer();

	
	private final double MAX_POWER = 0.84;  //was .88; was .93 Max power to run magazines polycord
	private final double MAX_RUNTIME = 2.0;  //Max seconds to run polycord as a timeout (tune to be a bit higher then the time it takes to move a ball through the magazine) 
	private double power = 0;
	private final double JAM_TIME = 0.75, JAM_AMP = 20; 
	
	private enum States {
		IDLE, 				//Motors stopped. May have ball.
		EMPTY,				//Motors stopped. No balls in magazine.  This occurs on timesout noted in other states.
		LOAD_BALL,			//Move ball into magazine until Bottom sensor clears of magazine fills
		BEGIN_LOAD_BREACH,	//Start moving balls toward Top sensor and start a timer
		LOAD_BREACH,		//Continue moving balls until Top sensor triggers or timer expires
		BREACH_LOADED,		//Stop moving balls and watch that breach stays loaded
		SHOOT_BALL, 		//Moves 1 ball to the flywheel.
		BEGIN_UNLOAD_BREACH,//Begin moving balls toward Bottom sensor and start a timer
		UNLOAD_BREACH,		//Continue moving balls until Bottom sensor triggers or timer expires
		BEGIN_DUMP_BALLS,	//Start moving balls outward and start timer
		DUMP_BALLS,			//Run magazine outward to dump any balls by waiting for timeout
		JAMMED;             //Magazine lock out if over current for too long
	}
	private States state = States.IDLE;
	/**
	 * Update state proccess to drive magazine.  Call every robot cycle.
	 *
	 * State logic:
	 * IDLE
	 *	Motors Off.  May contain a ball.
	 *	If Bottom sensor detects ball and Top sensor does not, goes to LOAD_BALL.
	 * LOAD_BALL
	 *	Run motors inward.
	 *	If no ball detected at Lower sensor or Top sensor detects ball, go to IDLE.
	 * LOAD_BREACH
	 *	Move balls to Top sensor
	 */
	public void update() {
		// Jam detection.
		if (Robot.getPDP().getCurrent(Constants.MAGAZINE_PDP_PORT) >= JAM_AMP) {
			if (jamTimer.get() > 0) {
				if (jamTimer.get() >= JAM_TIME) {
					stop();
					if (state == States.UNLOAD_BREACH)  {
						state = States.JAMMED;
					} else {
						state = States.BEGIN_UNLOAD_BREACH;
					}
				}
			} else {
				jamTimer.reset();
				jamTimer.start();
			}
		} else {
			jamTimer.stop();
			jamTimer.reset();
		}

		// State process
		switch (state) {
			case IDLE:
			// Motors Off.  May contain a ball.
			// If a ball is detected at the Bottom sensor and Magazine not full, then go to LOAD_BALL.
			// If a ball is detected only the Top sensor then go to BREACH_LOADED.
				stop();
				if (bottomSensorTriggered() && !fullyLoaded()) {
					state = States.LOAD_BALL;
				} else if (topSensorTriggered()) {
					state = States.BREACH_LOADED;
				}	
				break;
				
			case EMPTY:
			// Motors Off.  No balls in magazine.
			// Empty is assumed when breach LOAD_BREACH or UNLOAD_BREACH time out or DUMP_BALLS completes.
			// If a ball is detected at the Bottom sensor and Magazine not full, then go to LOAD_BALL.
				
				stop();
				if (bottomSensorTriggered() == true) {
					Common.debug("Mag: Load Ball");
					state = States.LOAD_BALL;
				}
				break;
				
			case LOAD_BALL:
			// Run motors inward.
			// Once ball is loaded past Lower sensor or Top sensor triggers, then go to IDLE.
				if (topSensorTriggered() == true){
					stop();
					Common.debug("Mag: Idle Top Sensor Triggered");
					state = States.IDLE;
				} else if (bottomSensorTriggered() == true) {
					load();
					Common.debug("Mag: loading bottom sensor triggered");
				} else {
					stop();
					Common.debug("Mag: Idle");
					state = States.IDLE;
				}
				break;
				
			case BEGIN_LOAD_BREACH:
			// This is initiated by loadBreach(), which would typically be called when Flywheel spins up.
			// If ball at Top sensor then BREACH_LOADED.
			// Otherwise, run motor toward Top sensor, initialize timeout timer for breach load to handle empty
			// magazine condition, and then go to LOAD_BREACH
				timer.reset();
				Common.debug("Mag: Load Breach");
				state = States.LOAD_BREACH;
				break;
				
			case LOAD_BREACH:
			// Look to see if ball has made it to the Top sensor.  If yes, then BREACH_LOADED.
			// If timeout timer runs out, then we mustn't have any balls in magazine, go to EMPTY.
			// Otherwise continue to run magaine motors inward.
				load();
				if (topSensorTriggered() == true) {
					stop();
					Common.debug("Breach Loaded");
					state = States.BREACH_LOADED;
				} else if (timer.get() >= MAX_RUNTIME) {
					Common.debug("Mag: Empty");
					state = States.EMPTY;
				}
				break;
				
			case BREACH_LOADED:
			// Stop the magazine motors.
			// If Top sensor looses sight of ball, then go to BEGIN_LOAD_BREACH.
				stop();
				if (topSensorTriggered() == false) {
					Common.debug("Mag: Begin Load Breach");
					state = States.BEGIN_LOAD_BREACH;
				}
				break;
			
			case SHOOT_BALL:
			//moves 1 ball in to flywheel.  When Top sensor clears, goes to BEGIN_LOAD_BREACH
				load();
				if (topSensorTriggered() == false) {
					Common.debug("Mag: Begin Load Breach");
					state = States.BEGIN_LOAD_BREACH;
				}
				break;

			case BEGIN_UNLOAD_BREACH:
			// This is initiated by unloadBreach(), which would typcially be called when the Flywheel spins down.
			// Look to see if a ball is already at the Bottom sensor.  If yes, then LOAD_BALL.
			// Otherwise, run motors toward Bottom sensor, begin timeout timer to handle empty magazine condition,
			// and then go to UNLOAD_BREACH:
				if (bottomSensorTriggered() == true) {
					Common.debug("Mag: Load Ball");
					state = States.LOAD_BALL;
				} else {
					timer.reset();
					unload();
					Common.debug("Mag: Unload Breach");
					state = States.UNLOAD_BREACH;
				}
				break;
			
			case UNLOAD_BREACH:
			// Continue to run the balls outward.  If the Bottom sensor triggers, then the unload is complete
			// and we just need to move that balls back in a bit past the Bottom sensor by going to LOAD_BALL.
			// If the timeout timer expires, then the magazine must be empty, so go to EMPTY state.
				if (bottomSensorTriggered() == true) {
					stop();
					Common.debug("Mag: Load Ball");
					state = States.LOAD_BALL;
				} else if (timer.get() >= MAX_RUNTIME) {
					Common.debug("Mag: Empty");
					state = States.EMPTY;
				}
				break;
				
			case BEGIN_DUMP_BALLS:
			// This state is intitiated by dumpBalls().
			// Start running motors outward and start a timeout timer.
			// Advance to DUMP_BALLS state.
				timer.reset();
				unload();
				Common.debug("Mag: Dump Balls");
				state = States.DUMP_BALLS;
				break;
				
			case DUMP_BALLS:
			// Continue to dump balls until timeout occurs. Go to EMPTY state.
				if (timer.get() >= MAX_RUNTIME) {
					Common.debug("Mag: Empty");
					state = States.EMPTY;
				} else {
					unload();
				}
				break;

			case JAMMED:
				stop();
				break;
		}  // End switch
	}	
	/**
	 * Set intake motor off and initialize state to IDLE.
	 */
	public void init(){
		state = States.IDLE;
		timer.reset();
		timer.start();
	}

	/**
	 * 
	 */
	public void debug(){
		//Common.dashNum("time elapsed", timer.get());
		//Common.dashBool("BOTTOM SENSOR TRIGGERED", bottomBeamBreak.get());
		Common.dashNum("Mag: Timer", timer.get());
		Common.dashStr("Mag: State", state.name());
		Common.dashNum("Mag: amps", Robot.getPDP().getCurrent(8));
		//Common.dashBool("Mag: TOP", topBeamBreak.get());
	}

	public boolean bottomSensorTriggered(){
		if (bottomBeamBreak.get()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean topSensorTriggered(){
		if (topBeamBreak.get()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	* If balls are seen at both the Top and Bottom sensor, then the magazine is considered to be fully loaded.
	* This does not necesarily mean that 5 balls are in the magazine, but it does mean that no more balls can
	* be loaded.
	*
	* @returns: boolean true when full.
 	*/
	public boolean fullyLoaded(){
		if (topSensorTriggered() && bottomSensorTriggered()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isShootBall() {
		return state == States.SHOOT_BALL;
	}

	public boolean isBeginLoadBreach() {
		return state == States.BEGIN_LOAD_BREACH;
	}

	public boolean isBreachLoaded() {
		return state == States.BREACH_LOADED;
	}

	public boolean isIdle() {
		return state == States.IDLE;
	}

	public boolean isJammed() {
		return state == States.JAMMED;
	}

	public boolean isBeginDumpBalls() {
		return state == States.BEGIN_DUMP_BALLS;
	}

	public boolean isDumpBalls() {
		return state == States.DUMP_BALLS;
	}
	
	/**
	 * The magazine enters an EMPTY state if balls were not seen during a load breach or unload breach action, or 
	 * a dump ball action occured.
	 *
	 * @returns: boolean true when magazine is assumed to be empty.
	 */
	public boolean isEmpty() {
		return state == States.EMPTY;
	}

	public boolean isReadyToIntake() {
		return state == States.IDLE || state == States.EMPTY || state == States.LOAD_BALL;
	}

	public boolean breachingStates() {
		return state == States.BEGIN_LOAD_BREACH || state == States.LOAD_BREACH || state == States.BREACH_LOADED;
	}
	
	public boolean dumpingStates() {
		return state == States.BEGIN_DUMP_BALLS || state == States.DUMP_BALLS;
	}
	/*
	* Power the magazine motor.
	* Positive values pull balls into the magazine. Negative values dump balls out.
	* The function will limit maxium allowed power output to MAX_POWER, to protect mechanics.
	* Always use the function to interact with motor power.
	*
	* @param power - Floating point value between -1.0 and +1.0
	*/
	private void setPower(double power) {
		if (power >= MAX_POWER) {
			power = MAX_POWER;
		} else if (power <= -MAX_POWER) {
			power = -MAX_POWER;
		}
		magMot.set(-power);
	}

 	/**
	 * Stop the magazine motor.
	 */
	public void stop() {
		power = 0.0;
		setPower(power);
	}
	
	/**
	 * Power the magazine in the loading (inward) direction.
	 * Power for loading is a constant, based on observation of best mechanical performance.
	 * This function does NOT employ any safety checks.
	 */
	private void load(){
			power = MAX_POWER;
			setPower(power);
	}

	/**
	 * Power the magazine in the unloading (outward) direction.
	 * Power for loading is a constant, based on observation of best mechanical performance.
	 * This function does NOT employ any safety checks.
	 */
	private void unload() {
		power = -MAX_POWER;
		setPower(power);
	}


	/**
	 * Initiate shooting a ball, but only if the breach is loaded.
	 * You can call this function repeatedly without harm (such as when right trigger is pressed).
	 * A quick trigger pull will shoot one ball (assuming a ball is already in the breach), whereas holding
	 * the trigger will shoot multiple balls automatically as the magazine cycles automatically to reloading the breach.
	 * 
	 * Logically, this function sets the magazine state to BEGIN_SHOOT_BALL, if the magazine is in a BREACH_LOADED state.
	 * SHOOT_BALL then advances one ball past the top sensor into the flywheel. This initiates a LOAD_BREACH state
	 * to load the next ball, if any, and make it ready to shoot.
	 */
	public void shootBall() {
		if (state == States.BREACH_LOADED) {
			state = States.SHOOT_BALL;
		}
	}
	
	/**
	 * Initiates a purge of balls from the magazine. 
	 *
	 * ??? This should only be allowed if Intake is Up --OR-- Intake is Down and running outward.
	 * ??? Let's work on this later.
	 */
	public void dumpBalls() {
			state = States.BEGIN_DUMP_BALLS;
	}
	
	/**
	 * This function initiates moving the balls toward the Top sensor, so that they are ready to shoot.
	 * If the magazine is in an EMPTY state, then breach loading will not occur.
	 * This action would normally only take place if the flywheel is spinning, in preparation to throw.
	 * The Shooter object is likely to use of this function, whenever initiating flywheel spin up, or autonomous mode
	 * could use this to get balls prepped for shooting.
	 *
	 * It is possible that the magazine is empty. BEGIN_LOAD_BREACH starts a timer to run the magazine for upto MAX_RUNTIME
	 * seconds. If a ball does not reach the Top sensor by then, the magazine will go to EMPTY state, which is like IDLE,
	 * but with the knowledge that no balls are in the magazine.  This can be useful information to either display on the
	 * driverstation or to prevent allowing the flywheel to spin up when no balls are availalbe.   
	 */
	public void loadBreach() {
		if (state == States.IDLE || state == States.JAMMED) {
			state = States.BEGIN_LOAD_BREACH;
		}
	}
	
	/**
	 * This function is used to back balls away from the Breach and move them back to the Bottom sensor, where more
	 * balls can be loaded into the magazine.  This function would normally be called when the Flywheel spins down to idle.
	 * 
	 * If the Magazine is either preparing to load the breach, or the breach is already loaded, then BEGIN_UNLEAD_BREACH
	 * will be intiated.
	 *
	 */
	public void unloadBreach() {
		if (state == States.BREACH_LOADED || state == States.BEGIN_LOAD_BREACH || state == States.JAMMED) {
			state = States.BEGIN_UNLOAD_BREACH;
		}
	}

} // End Magazine class
