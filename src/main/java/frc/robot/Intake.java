/**
 * Handles the entirety of the intake system.
 * @author Swaroop Handral
 */

package frc.robot;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.Timer;
/**
 * The Intake sub-system consists of intake motor(s) for powercell collection or dumping, and
 * pnuematically powered arms to raise and lower the intake. 
 *
 * Intake must be instantiated by the Shooter object, which will provide access to the Magazine instance.
 * State logic drives the intake and ensures proper collaboration with the Magazine.
 * 
 * Run init() when starting the robot and then call update() every robot cycle to drive the state process.
 */
public class Intake {
	
	private final static Spark intakeMot = new Spark(Constants.PWM_INTAKE_MOTOR);
	private final static Solenoid outSol = new Solenoid(Constants.PCM_CAN_ID, Constants.SOL_INTAKE_OUT_ARM);	
	private final static Solenoid inSol  = new Solenoid(Constants.PCM_CAN_ID, Constants.SOL_INTAKE_IN_ARM);
	private final static double MAX_POWER = 1; // This is a placeholder constant.
	private Timer timer = new Timer();  
	private final double MAX_RUNTIME = 2.0;
	private States state = States.IDLE;
	private double power = 0;
	private double clearWait = Common.time();

	private enum States {
		IDLE, 			//No motor movement, intake is up.
		LOADING,		//Entered through a button press. Intake is down. Remains in this state until adjusted through button input or five balls have been loaded.
		START_CLEAR, 	//sets the timer for the clear function
		CLEAR,			//Will wait for the intake to be up then clear the intake of any stuck bowls.
		EJECTING;       //Entered through a button press, runs the polycord outwards 5x, sets intake down. Remains in this state until adjusted through button input.

	}
	
	/**
	 * ??? We need to talk more about this initialization requirements.  This might be
	 * ??? nothing more than setting state to IDLE.
	 * Initialize intake in up position with intake motors off and set intake to IDLE.
	 * This function should be called when enabling the robot.
	 * You may want to protect from calling this function during the 
	 * FMS-initiated trasistion from Autonomous to Teleop.
	 */
	public void init(){
		state = States.IDLE;
		timer.reset();
		timer.start();
	}

	/**
	 * Display Intake-specific debug data to Smartdashboard and/or console.
	 */
	public void debug(){
		Common.dashStr("IN: State", state.toString());
	}

	public boolean isIdle() {
		return state == States.IDLE;
	}

	public boolean isLoading() {
		return state == States.LOADING;
	}

	public boolean isEjecting() {
		return state == States.EJECTING;
	}

	/**
	 * Sets the intake motor power.  Positive values will run motes inward.
	 * Will limit the maximum power sent to the motor based on MAX_POWER.
	 */
	private void setMotorPower(double power) {
		if (power > MAX_POWER) {
			power = MAX_POWER;
		}
		else {
			if (power < -MAX_POWER ) {
				power = -MAX_POWER;
			}
		}		
		intakeMot.set(power);
	}

	/**
	* Turn off the intake motor(s).
	*/
	public void motorStop(){
		power = 0;
		setMotorPower(power);
	}

	/**
	 * Run the intake motor(s) inward to pickup power cells. 
	 * The intake speed is a constant, based on observation of best mechanical performance.
	 * This function does NOT employ safeties to protect from overloading the magazine.
	 */
	private void motorIn(){
		power = MAX_POWER;
		setMotorPower(power);
	}

	/**
	 * Runs the intake motor(s) outwards to dump power cells. 
	 * The motor speed is a constant, based on observation of best mechanical performance.
	 */
	private void motorOut(){
		power = -MAX_POWER;
		setMotorPower(power);
	}

	/**
	 * Set intake arms in up position through pnuematic control.
	 * Intake motor(s) will be stopped.
	 */
	private void setIntakeUp(){
		motorStop();
		outSol.set(false);
		inSol.set(true);
	}

	/**
	 * Set intake arms in down position through pnuematic control.
	 */
	private void setIntakeDown() {
		inSol.set(false);
		outSol.set(true);
	}

	/** 
	 * Call this function to initiate intaking balls.
	 * 
	 * The state must be IDLE for this to occur.
	*/
	public void startIntake() {
		if (state == States.IDLE); {
			state = States.LOADING;
		}
	}

	/**
	 *  Cancels ball intake.
	 * 
	 * The state must be LOADING
	 */
	public void stopIntake() {
		if (state == States.LOADING) {
			state = States.START_CLEAR; 
			//state = States.IDLE;
		}
	}

	public void ejectingIntake() {
		if (state == States.IDLE) {
			timer.reset();
			state = States.EJECTING; 
		}
	}


	/**
	 * 
	 */
	public void update() {
		switch(state) {
			case IDLE:
				setIntakeUp();
				motorStop();
				break;

			case LOADING:
				setIntakeDown();
				motorIn();
				break;

			case START_CLEAR:
				setIntakeUp();
				clearWait = Common.time();
				state = States.CLEAR;
				break;

			case CLEAR:
				if ((clearWait + 500) >= Common.time()) {
					//is waiting for the pneumatics
				} else if ((clearWait  + 1500) >= Common.time()) {
					motorOut();
				} else {
					state = States.IDLE;
				}
				break;

			case EJECTING:
				setIntakeDown();
				motorOut();
				if (timer.get() >= MAX_RUNTIME) {
					state = States.IDLE;	
				}
				break;

		}

	}
}
