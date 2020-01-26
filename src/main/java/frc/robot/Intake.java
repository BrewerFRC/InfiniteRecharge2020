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
	private final static Solenoid arm = new Solenoid(Constants.SOL_INTAKE_ARM);
	private final static double MAX_POWER = .2; // This is a placeholder constant.
	private Timer timer = new Timer();  
	private final double MAX_RUNTIME = 2.0;
	private States state = States.IDLE;

	private enum States {
		IDLE, 			//No motor movement, intake is up.
		LOAD,			// Entered through a button press, constantly runs the polycord inwards. Intake is down. Remains in this state until adjusted through button input or five balls have been loaded.
		EJECT;          //Entered through a button press, runs the polycord outwards 5x, sets intake down. Remains in this state until adjusted through button input.

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
	}

	/**
	 * Display Intake-specific debug data to Smartdashboard and/or console.
	 */
	public void debug(){

	}

	/**
	 * Run the intake motor(s) inward to pickup power cells. 
	 * The intake speed is a constant, based on observation of best mechanical performance.
	 * This function does NOT employ safeties to protect from overloading the magazine.
	 */
	public void posIntake(){
		setMotorPower(MAX_POWER);
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
	public void stopIntake(){
		setMotorPower(0);
	}

	/**
	 * Runs the intake motor(s) outwards to dump power cells. 
	 * The motor speed is a constant, based on observation of best mechanical performance.
	 */
	public void negIntake(){
		setMotorPower(-MAX_POWER);
	}

	/**
	 * Set intake arms in up position through pnuematic control.
	 * Intake motor(s) will be stopped.
	 */
	public void setIntakeUp(){
		stopIntake();
		arm.set(true);
	}

	/**
	 * Set intake arms in down position through pnuematic control.
	 */
	public void setIntakeDown() {
		arm.set(false);
	}
	
	/**
	 * 
	 */
	public void update() {
		switch(state) {
			case IDLE:
				setIntakeUp();
				stopIntake();
				break;

			case LOAD:
				setIntakeDown();
				posIntake();
				break;

			case EJECT:
				setIntakeDown();
				negIntake();
				if (timer.get() >= MAX_RUNTIME) {
					state = States.IDLE;	
				}
				break;

		}

	}
}
