package frc.robot;

/**
 * The Intake sub-system consists of intake motor(s) for powercell collection or dumping, and
 * pnuematically powered arms to raise and lower the intake. 
 *
 * Intake must be instiated by the Shooter object, which will provide access to the Magazine instance.
 * State logic drives the intake and ensures proper collaboration with the Magazine.
 * 
 * Run init() when starting the robot and then call update() every robot cycle to drive the state process.
 */
public class Intake {

	/**
	 * ??? We need to talk more about this initialization requirements.  This might be
	 * ??? nothing more than setting state to IDLE.
	 * Initiliaze intake in up position with intake motors off and set intake to IDLE.
	 * This function should be called when enabling the robot.
	 * You may want to protect from calling this function during the 
	 * FMS-initiated trasistion from Autonomous to Teleop.
	 */
	public void init(){

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

	}

	/**
	* Turn off the intake motor(s).
	*/
	public void stopIntake(){

	}

	/**
	 * Run the intake motor(s) outwards to dump power cells. 
	 * The motor speed is a constant, based on observation of best mechanical performance.
	 */
	public void negIntake(){

	}

	/**
	 * Set intake arms in up position through pnuematic control.
	 * Intake motor(s) will be stopped.
	 */
	public void setIntakeUp(){

	}

	/**
	 * Set intake arms in down position through pnuematic control.
	 */
	public void setIntakeDown() {

	}
	
	/**
	 * 
	 */
	public void update() {
		
	}
}
