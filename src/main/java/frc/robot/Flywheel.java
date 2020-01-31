package frc.robot;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkMax;
import com.revrobotics.CANSparkMax.FaultID;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.Solenoid;

public class Flywheel {
    private final static CANSparkMax flywheel_left = new CANSparkMax(Constants.FLYWHEEL_LEFT_CAN_ID, MotorType.kBrushless);
    private final static CANSparkMax flywheel_right = new CANSparkMax(Constants.FLYWHEEL_RIGHT_CAN_ID, MotorType.kBrushless);
    private final static Solenoid hood = new Solenoid(Constants.SOL_FLAPPER);
    private double targetPower, targetRPM, minRPM;  

    private CANPIDController left_pidController, right_pidController;
    private CANEncoder left_encoder, right_encoder;

    private enum States {
        IDLE,
        SPIN_UP,
        READY_TO_THROW;
    }
    private States state = States.IDLE;
    private final static double LONG_POWER = 1, MEDIUM_POWER = 0.7, SHORT_POWER = 0.5; 
    private final static int LONG_RPM = 5000, MEDIUM_RPM = 4000, SHORT_RPM = 800;
    private final static int LONG_MIN = 4800, MEDIUM_MIN = 3800, SHORT_MIN = 700;

    /**
     * FlyWheel.
	Idle -
		No motors will run in this state
	Spin Up -
		This state will begin upon a button press
		The flywheel will spin up to RPM target
		Moves from this state to Ready to Throw upon reaching the RPM
	Ready to Throw - 
		This state will be entered as Spin Up reaches it completion
		Will retain the RPM of the flywheel at target
        Retain until RPM drops below minimum moves to Spin Up
        
     */
    public void init(){
            /**
        * The RestoreFactoryDefaults method can be used to reset the configuration parameters
        * in the SPARK MAX to their factory default state. If no argument is passed, these
        * parameters will not persist between power cycles
        */
        flywheel_left.restoreFactoryDefaults();
        flywheel_right.restoreFactoryDefaults();
        left_pidController = flywheel_left.getPIDController();
        right_pidController = flywheel_right.getPIDController();
        left_encoder = flywheel_left.getEncoder();
        right_encoder = flywheel_right.getEncoder();
      

    }

    /**
     * 
     */
	public void debug(){

    }

    /**
     * change our state to spin_up, make fly_wheel and max_spark is ready, make sure the max_spark rpm is inbetween the set and mimimal rpm threshold, the set stait to ready_to_lonch
     *@param distance is string of either long, medium, and small to change the fly_wheel moter
     */
	public void start(String distance){
        switch (distance) {
            case "long" :
                targetPower = LONG_POWER;
                targetRPM = LONG_RPM;
                minRPM = LONG_MIN;
                hoodDown();
                break;
            case "medium" :
                targetPower = MEDIUM_POWER;
                targetRPM = MEDIUM_RPM;
                minRPM = MEDIUM_MIN;
                hoodDown();
                break;
            case "short" :
                targetPower = SHORT_POWER;
                targetRPM = SHORT_RPM;
                minRPM = SHORT_MIN;
                hoodUp();
                break;           
            default :
                Common.debug("FW: Bad distance parameter");   
                targetPower = 0;
                targetRPM = 0;
                minRPM = 0; 
                break;
        }
        state = States.SPIN_UP;
    } 
    

    /**
     * stop the flywheel by setting IDLE state
     */
	public void stop(){
        state = States.IDLE;
       
    }

    

    /**
     * 
     */
	public void hoodUp(){
        hood.set(true);
    }

    /**
     * 
     */
	public void hoodDown(){
        hood.set(false);
    }

    /**
     * 
     */
	public int getRPM(){
        return 0;
    }

	/*public boolean atRPM(){
        
    } */
}