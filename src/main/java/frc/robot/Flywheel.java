package frc.robot;

import com.revrobotics.CANEncoder;
import edu.wpi.first.wpilibj.DigitalInput;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkMax;
import com.revrobotics.CANSparkMax.FaultID;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.Solenoid;

public class Flywheel {
    private final static CANSparkMax flywheelLeft = new CANSparkMax(Constants.FLYWHEEL_LEFT_CAN_ID, MotorType.kBrushless);
    private final static CANSparkMax flywheelRight = new CANSparkMax(Constants.FLYWHEEL_RIGHT_CAN_ID, MotorType.kBrushless);
    private final static Solenoid hood = new Solenoid(Constants.SOL_FLAPPER);
    private double targetPower, targetRPM, toleranceRPM;

    
    private CANPIDController left_pidController, right_pidController;
    private CANEncoder left_encoder, right_encoder;

    private enum States {
        IDLE,
        SPIN_UP,
        READY_TO_FIRE;
    }

    public enum speed {
        SHORT,
        MEDIUM,
        LONG;
    }

    private States state = States.IDLE;
    private final static double LONG_POWER = 1, MEDIUM_POWER = 0.7, SHORT_POWER = 0.5; 
    private final static double LONG_RPM = 5000, MEDIUM_RPM = 4000, SHORT_RPM = 800;
    private final static double LONG_TOLERANCE = 200, MEDIUM_TOLERANCE = 200, SHORT_TOLERANCE = 100;

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
        flywheelLeft.restoreFactoryDefaults();
        flywheelRight.restoreFactoryDefaults();
        flywheelLeft.setIdleMode(CANSparkMax.IdleMode.kCoast);
        flywheelRight.setIdleMode(CANSparkMax.IdleMode.kCoast);
        left_pidController = flywheelLeft.getPIDController();
        right_pidController = flywheelRight.getPIDController();
        left_encoder = flywheelLeft.getEncoder();
        right_encoder = flywheelRight.getEncoder();
      

    }

    /**
     * 
     */
	public void debug(){
        Common.dashNum("encoder velocity", getRPM());
    }

    private void setMotors(double power){
        flywheelLeft.set(power);
        flywheelRight.set(-power);
    }

    /**
     * change our state to spin_up, make fly_wheel and max_spark is ready, make sure the max_spark rpm is inbetween the set and mimimal rpm threshold, the set stait to ready_to_lonch
     *@param distance is string of either long, medium, and small to change the fly_wheel moter
     */
	public void start(speed distance){
        switch (distance) {
            case LONG :
                targetPower = LONG_POWER;
                targetRPM = LONG_RPM;
                toleranceRPM = LONG_TOLERANCE;
                hoodDown();
                break;
            case MEDIUM :
                targetPower = MEDIUM_POWER;
                targetRPM = MEDIUM_RPM;
                toleranceRPM = MEDIUM_TOLERANCE;
                hoodDown();
                break;
            case SHORT :
                targetPower = SHORT_POWER;
                targetRPM = SHORT_RPM;
                toleranceRPM = SHORT_TOLERANCE;
                hoodUp();
                break;           
            default :
                Common.debug("FW: Bad distance parameter");   
                targetPower = 0;
                targetRPM = 0;
                toleranceRPM = 0; 
                break;
        }
        state = States.SPIN_UP;
    } 
    
    public boolean isIdle() {
        return (state == States.IDLE);
    }

    /**
     * stop the flywheel by setting IDLE state
     */
	public void stop() {
        setMotors(0);  
    }

    /**
     * put the hood up for short
     */
	private void hoodUp(){
        hood.set(true);
    }

    /**
     * put the hood down for long and medium
     */
	private void hoodDown(){
        hood.set(false);
    }

    /**
     * 
     */
	public double getRPM(){
        return (left_encoder.getVelocity() + -right_encoder.getVelocity()) /2;
    }

    /**
     * tells us if we're ready to throw
     * @return
     */
	private boolean atRPM(){
        return Common.between(getRPM(), targetRPM - toleranceRPM, targetRPM + toleranceRPM);
    } 
    public boolean readyToFire() {
        return (state == States.READY_TO_FIRE);
    }

    public void update(){
        switch (state) {
            case IDLE :
                stop();
                break;
            case SPIN_UP :
                setMotors(targetPower);
                if (atRPM()) {
                    state = States.READY_TO_FIRE;
                }
                break;
            case READY_TO_FIRE :
                if (!atRPM()) {
                    state = States.SPIN_UP;
                }
                break;
        }
                
    }
    
}