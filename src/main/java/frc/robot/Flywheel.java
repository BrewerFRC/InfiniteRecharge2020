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
    private final static Solenoid hood = new Solenoid(Constants.SOL_FLAPPER);
    private double targetPower, targetRPM, toleranceRPM, setPoint, processVariable;

    
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
    private final static double LONG_POWER = 1, MEDIUM_POWER = 0.7, SHORT_POWER = 0.5; 
    private final static double LONG_RPM = 5000, MEDIUM_RPM = 4000, SHORT_RPM = 2000;
    private final static double LONG_TOLERANCE = 200, MEDIUM_TOLERANCE = 200, SHORT_TOLERANCE = 100;
    public double kP, kI, kD, kIz, kFF, kMaxOutput, kMinOutput, maxRPM, maxVel, minVel, maxAcc, allowedErr;

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
        // PID coefficients
        kP = 5e-5; 
        kI = 1e-6;
        kD = 0; 
        kIz = 0; 
        kFF = 0.000156; 
        kMaxOutput = 1; 
        kMinOutput = -1;
        maxRPM = 5000;

        // Smart Motion Coefficients
        maxVel = 5000; // rpm
        maxAcc = 1500;

         // set PID coefficients
        left_pidController.setP(kP);
        left_pidController.setI(kI);
        left_pidController.setD(kD);
        left_pidController.setIZone(kIz);
        left_pidController.setFF(kFF);
        left_pidController.setOutputRange(kMinOutput, kMaxOutput);

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
        int smartMotionSlot = 0;
        left_pidController.setSmartMotionMaxVelocity(maxVel, smartMotionSlot);
        left_pidController.setSmartMotionMinOutputVelocity(minVel, smartMotionSlot);
        left_pidController.setSmartMotionMaxAccel(maxAcc, smartMotionSlot);
        left_pidController.setSmartMotionAllowedClosedLoopError(allowedErr, smartMotionSlot);
        flywheelRight.follow(flywheelLeft, true);

      

    }

    /**
     * 
     */
	public void debug(){
        Common.dashNum("FW: encoder velocity", getRPM());
        // display PID coefficients on SmartDashboard
        Common.dashNum("FW: P Gain", kP);
        Common.dashNum("FW: I Gain", kI);
        Common.dashNum("FW: D Gain", kD);
        Common.dashNum("FW: I Zone", kIz);
        Common.dashNum("FW: Feed Forward", kFF);
        Common.dashNum("FW: Max Output", kMaxOutput);
        Common.dashNum("FW: Min Output", kMinOutput);

        // display Smart Motion coefficients
        Common.dashNum("FW: Max Velocity", maxVel);
        Common.dashNum("FW: Min Velocity", minVel);
        Common.dashNum("FW: Max Acceleration", maxAcc);
        Common.dashNum("FW: Allowed Closed Loop Error", allowedErr);
        Common.dashNum("FW: Set Position", 0);
        Common.dashNum("FW: Set Velocity", 0);

        // button to toggle between velocity and smart motion modes
        Common.dashBool("FW: Mode", true);
        Common.dashNum("SetPoint", setPoint);
        Common.dashNum("Process Variable", processVariable);
        Common.dashNum("Output", flywheelLeft.getAppliedOutput());

    }

    private void setMotors(double power){
        //flywheelLeft.set(power);
        //flywheelRight.set(-power);
        setPoint = Common.getNum("FW: Set Velocity", 0);
        left_pidController.setReference(setPoint, ControlType.kVelocity);
        processVariable = left_encoder.getVelocity();
    }

    /**
     * change our state to spin_up, make fly_wheel and max_spark is ready, make sure the max_spark rpm is inbetween the set and mimimal rpm threshold, the set stait to ready_to_lonch
     *@param distance is string of either long, medium, and small to change the fly_wheel moter
     */
	public void start(Distance distance){

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
        state = States.IDLE;
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
        // read PID coefficients from SmartDashboard
        double p = Common.getNum("P Gain", 0);
        double i = Common.getNum("I Gain", 0);
        double d = Common.getNum("D Gain", 0);
        double iz = Common.getNum("I Zone", 0);
        double ff = Common.getNum("Feed Forward", 0);
        double max = Common.getNum("Max Output", 0);
        double min = Common.getNum("Min Output", 0);
        double maxV = Common.getNum("Max Velocity", 0);
        double minV = Common.getNum("Min Velocity", 0);
        double maxA = Common.getNum("Max Acceleration", 0);
        double allE = Common.getNum("Allowed Closed Loop Error", 0);


        // if PID coefficients on SmartDashboard have changed, write new values to controller
        if((p != kP)) { left_pidController.setP(p); kP = p; }
        if((i != kI)) { left_pidController.setI(i); kI = i; }
        if((d != kD)) { left_pidController.setD(d); kD = d; }
        if((iz != kIz)) { left_pidController.setIZone(iz); kIz = iz; }
        if((ff != kFF)) { left_pidController.setFF(ff); kFF = ff; }
        if((max != kMaxOutput) || (min != kMinOutput)) { 
            left_pidController.setOutputRange(min, max); 
            kMinOutput = min; kMaxOutput = max; 
        }

        if((maxV != maxVel)) { left_pidController.setSmartMotionMaxVelocity(maxV,0); maxVel = maxV; }
        if((minV != minVel)) { left_pidController.setSmartMotionMinOutputVelocity(minV,0); minVel = minV; }
        if((maxA != maxAcc)) { left_pidController.setSmartMotionMaxAccel(maxA,0); maxAcc = maxA; }
        if((allE != allowedErr)) { left_pidController.setSmartMotionAllowedClosedLoopError(allE,0); allowedErr = allE; }
        
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