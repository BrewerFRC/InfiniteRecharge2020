package frc.robot;

import com.revrobotics.CANEncoder;
import edu.wpi.first.wpilibj.DigitalInput;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.revrobotics.SparkMax;
import com.revrobotics.CANSparkMax.FaultID;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.Solenoid;

public class Flywheel {
    private final static CANSparkMax flywheelLeft = new CANSparkMax(Constants.FLYWHEEL_LEFT_CAN_ID, MotorType.kBrushless);
    private final static CANSparkMax flywheelRight = new CANSparkMax(Constants.FLYWHEEL_RIGHT_CAN_ID, MotorType.kBrushless);
    //private final static Solenoid hood = new Solenoid(Constants.SOL_FLAPPER);
    private double targetPower, targetRPM, toleranceRPM, setPoint = 0, processVariable;

    
    private CANPIDController left_pidController, right_pidController;
    private CANEncoder left_encoder, right_encoder;

    private enum States {
        IDLE,
        SPIN_UP,
        READY_TO_FIRE;
    }

    public enum Distance {
        SHORT,
        MEDIUM,
        LONG;
    }

    private States state = States.IDLE;
    private final static double LONG_RPM = 5100, MEDIUM_RPM = 5000, SHORT_RPM = 2500;
    private final static double LONG_TOLERANCE = 500, MEDIUM_TOLERANCE = 500
    , SHORT_TOLERANCE = 50;
    private double kP, kI, kD, kIz, kFF, kMaxOutput, kMinOutput;
    // SMARTMOTION VARIABLES - CAN THESE BE REMOVED?
    private double maxVel, minVel, maxAcc, allowedErr;

    /**
     * FlyWheel.
	Idle -
		No motors will run in this state
	Spin Up -
		This state will begin when start() is called.
		The flywheel will spin up to RPM target
		Moves from this state to Ready to Throw upon reaching the allowable RPM range
	Ready to Throw - 
		This state will be entered as Spin Up reaches it completion
		Will retain the RPM of the flywheel at target
        If RPM goes outside of allowable range, goes back to Spin Up state      
     */
    public Flywheel() {
        flywheelLeft.restoreFactoryDefaults();
        flywheelRight.restoreFactoryDefaults();
        flywheelLeft.setIdleMode(CANSparkMax.IdleMode.kCoast);
        flywheelRight.setIdleMode(CANSparkMax.IdleMode.kCoast);
        left_pidController = flywheelLeft.getPIDController();
        right_pidController = flywheelRight.getPIDController();
        left_encoder = flywheelLeft.getEncoder();
        right_encoder = flywheelRight.getEncoder();
        left_encoder.setVelocityConversionFactor(42.0/36);
        right_encoder.setVelocityConversionFactor(42.0/36);
	    flywheelRight.follow(flywheelLeft, true);  //set follow with inversion

        // PID coefficients
        kP = 0.001; 
        kI = 0;
        kD = 10; 
        kIz = 0; 
        kFF = 0.00018; 
        kMaxOutput = 1.0; 
        kMinOutput = 0;
        
         // set PID coefficients for left motor only, since right motor will just follow
        left_pidController.setP(kP);
        left_pidController.setI(kI);
        left_pidController.setD(kD);
        left_pidController.setIZone(kIz);
        left_pidController.setFF(kFF);
        left_pidController.setOutputRange(kMinOutput, kMaxOutput);

        //CAN WE REMOVE SMARTMOTION?
        /*
    	// Smart Motion Coefficients
        maxVel = 5000; // rpm
        maxAcc = 1500;
        */
	    /**
        * Smart Motion coefficients are set on a CANPIDController object
        * 
        * - setSmartMotionMaxVelocity() will limit the velocity in RPM of
        * the pid controller in Smart Motion mode
        * - setSmartMotionMinOutputVelocity() will put a lower bound in
        * RPM of the pid controller in Smart Motion mode
        * - setSmartMotionMaxAccel() will limit the acceleration in RPM^2
        * of the pid controller in Smart Motion mode
        * - setSmartMotionAllowedClosedLoopError() will set the max allowed
        * error for the pid controller in Smart Motion mode
        */
        /*
        int smartMotionSlot = 0;
        left_pidController.setSmartMotionMaxVelocity(maxVel, smartMotionSlot);
        left_pidController.setSmartMotionMinOutputVelocity(minVel, smartMotionSlot);
        left_pidController.setSmartMotionMaxAccel(maxAcc, smartMotionSlot);
        left_pidController.setSmartMotionAllowedClosedLoopError(allowedErr, smartMotionSlot);
        */
	
        // TESTING ===================
        // Populate smartdashboard with the initial paramter values.
        debug();
        //============================
    }

    public void init() {
        state = States.IDLE;

    }
   
    /** 
     * change our state to spin_up, make fly_wheel and max_spark is ready, make sure the max_spark rpm is inbetween the set and mimimal rpm threshold, the set stait to ready_to_lonch
     *@param distance is string of either long, medium, and small to change the fly_wheel moter
     */
	public void start(Distance distance){

        switch (distance) {
            case LONG :
                targetRPM = LONG_RPM;
                toleranceRPM = LONG_TOLERANCE;
                hoodDown();
                break;
            case MEDIUM :
                targetRPM = MEDIUM_RPM;
                toleranceRPM = MEDIUM_TOLERANCE;
                hoodDown();
                break;
            case SHORT :
                targetRPM = SHORT_RPM;
                toleranceRPM = SHORT_TOLERANCE;
                hoodUp();
                break;           
        }
        state = States.SPIN_UP;
        // TESTING =============
        setRPM(targetRPM);
        // =====================
    } 
    
    /**
     * Is flywheel idle?
     */
    public boolean isIdle() {
        return (state == States.IDLE);
    }

    /**
     * Stop the flywheel by setting IDLE state
     */
	public void stop() {
        state = States.IDLE;
    }

    /**
     * Put the hood up for short shots
     */
	private void hoodUp(){
        //hood.set(true);
    }

    /**
     * Put the hood down for long and medium shots
     */
	private void hoodDown(){
        //hood.set(false);
    }

    /**
     * Set motor RPM for PID using targetRPM.
     */
    private void setRPM(double RPM) {
        // TESTING ===============
        Common.dashNum("FW: targetRPM", RPM);
        // =======================
        // Set RPM of left motor. Right motor follows.
        left_pidController.setReference(RPM, ControlType.kVelocity);
        //processVariable = left_encoder.getVelocity();
    }

    /**
     * Current flywheel RPM
     */
	private double getRPM(){
        return left_encoder.getVelocity() ;
        //return (-left_encoder.getVelocity() + right_encoder.getVelocity()) /2;
    }

    /**
     * Returns true if RPM is with accpetable range to shoot.
     */
	private boolean atRPM(){
        return Common.between(getRPM(), targetRPM - toleranceRPM, targetRPM + toleranceRPM);
    }
    
    /**
    * Is firewheel in ready state to shoot?
    */
    public boolean readyToFire() {
        return (state == States.READY_TO_FIRE);
    }

    /**
    * Update flywheel PID and state logic.
    * Call every robot cycle.
    */
    public void update(){
        double rpm = Common.getNum("FW: targetRPM",0);
        // TESTING ===================================
        // read PID coefficients from SmartDashboard
        double p = Common.getNum("FW: P Gain", kP);
        double i = Common.getNum("FW: I Gain", kI);
        double d = Common.getNum("FW: D Gain", kD);
        double iz = Common.getNum("FW: I Zone", kIz);
        double ff = Common.getNum("FW: Feed Forward", kFF);
        double max = Common.getNum("FW: Max Output", kMinOutput);
        double min = Common.getNum("FW: Min Output", kMaxOutput);
        //double maxV = Common.getNum("Max Velocity", 0);
        //double minV = Common.getNum("Min Velocity", 0);
        //double maxA = Common.getNum("Max Acceleration", 0);
        //double allE = Common.getNum("Allowed Closed Loop Error", 0);

        // if PID coefficients on SmartDashboard have changed, write new values to controller
        if((p != kP)) { left_pidController.setP(p); kP = p; }
        if((i != kI)) { left_pidController.setI(i); kI = i; }
        if((d != kD)) { left_pidController.setD(d); kD = d; }
        if((iz != kIz)) { left_pidController.setIZone(iz); kIz = iz; }
        if((ff != kFF)) { left_pidController.setFF(ff); kFF = ff; }
        if((max != kMaxOutput) || (min != kMinOutput)) { 
            left_pidController.setOutputRange(min, max); 
        }
        
        //if((maxV != maxVel)) { left_pidController.setSmartMotionMaxVelocity(maxV,0); maxVel = maxV; }
        //if((minV != minVel)) { left_pidController.setSmartMotionMinOutputVelocity(minV,0); minVel = minV; }
        //if((maxA != maxAcc)) { left_pidController.setSmartMotionMaxAccel(maxA,0); maxAcc = maxA; }
        //if((allE != allowedErr)) { left_pidController.setSmartMotionAllowedClosedLoopError(allE,0); allowedErr = allE; }
        // ===========================================
        
        switch (state) {
            case IDLE :
                targetRPM = 0;
                setRPM(targetRPM);
                break;
            case SPIN_UP :
                setRPM(targetRPM);
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
        debug();
    }

     /**
     *  Display flywheel debug 
     */
	public void debug(){
        Common.dashBool("FW: at RPM", atRPM());
        Common.dashNum("FW: targetRPM", targetRPM);
        Common.dashNum("FW: encoder rpm", getRPM());
        Common.dashNum("FW: L Output", flywheelLeft.getAppliedOutput());
        Common.dashNum("FW: R Output", flywheelRight.getAppliedOutput());
        Common.dashNum("FW: L Amps", flywheelLeft.getOutputCurrent());
        Common.dashNum("FW: R Amps", flywheelRight.getOutputCurrent());

        Common.dashStr("FW: State", state.toString());

        // display PID coefficients on SmartDashboard
        Common.dashNum("FW: P Gain", kP);
        Common.dashNum("FW: I Gain", kI);
        Common.dashNum("FW: D Gain", kD);
        Common.dashNum("FW: I Zone", kIz);
        Common.dashNum("FW: Feed Forward", kFF);
        Common.dashNum("FW: Max Output", kMaxOutput);
        Common.dashNum("FW: Min Output", kMinOutput);

        /*
        // display Smart Motion coefficients
        Common.dashNum("FW: Max Velocity", maxVel);
        Common.dashNum("FW: Min Velocity", minVel);
        Common.dashNum("FW: Max Acceleration", maxAcc);
        Common.dashNum("FW: Allowed Closed Loop Error", allowedErr);
        Common.dashNum("FW: Set Position", 0);
        Common.dashNum("FW: Set Velocity", 0);
        */
        // button to toggle between velocity and smart motion modes
        //Common.dashBool("FW: Mode", true);
        //Common.dashNum("Process Variable", processVariable);        
    }
    
}
