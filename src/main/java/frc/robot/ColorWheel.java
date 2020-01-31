package frc.robot;

import edu.wpi.first.wpilibj.util.Color;
//import jdk.nashorn.internal.runtime.regexp.joni.constants.TargetInfo;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.DriverStation;

class ColorWheel{
    String gameData;
    ColorSensor colorSensor1;
    ColorSensor colorSensor2;
    Spark motor;
    String verifiedColorChar1, verifiedColorChar2; 
    double pieAmount1, pieAmount2;
    String targetColor;
    
    public static final double ROTATION_POWER = 1;
    public static final double POSITION_POWER = 0.6;
    public static final double MAX_POWER = 1;

    private enum States {
		IDLE, 			//Motors stopped. 
        START_COUNTING, //Resets counter starts motor and goes to COUNTING
        START_FINDING,  //Determine what color we're looking for 
        COUNTING,       //Counts how many rotations we've gone thought
        FINDING;        //Start looking for the color we're after
	}
    private States state = States.IDLE;

    public ColorWheel(Port one, Port two) {
        //make the sensor variables
        colorSensor1 = new ColorSensor(one);
        colorSensor2 = new ColorSensor(two);
        motor = new Spark(Constants.PWM_COLORWHEEL_MOTOR);
    }

    public void debug(){
        SmartDashboard.putString("S1-VerifiedColor", verifiedColorChar1);
    SmartDashboard.putString("S2-VerifiedColor", verifiedColorChar2);
    //SmartDashboard.putNumber("sensor Blue", detectedColorBlue);
    //SmartDashboard.putNumber("sensor red", detectedColorRed);
    //SmartDashboard.putNumber("sensor green", detectedColorGreen);
    //SmartDashboard.putString("colorChar", ""+colorChar);
    SmartDashboard.putNumber("S1-pieSlices", pieAmount1);
    SmartDashboard.putNumber("S2-pieSlices", pieAmount2);
    SmartDashboard.putString("targetColor", ""+targetColor); 
    SmartDashboard.putBoolean("targetColorVerified", targetColorVerified());
    SmartDashboard.putBoolean("rotationVerified", rotationVerified());
    }

    /**
     * Runs the motor to spin the colorwheel.  Positive values will
     * spin the wheel clockwise.
     * 
     * @param power
     */
    private void setMotor(double power){
        if (power > MAX_POWER) {
            power = MAX_POWER;
        } else {
            if (power < -MAX_POWER) {
                power = -MAX_POWER;
            } 
        motor.set(power);
        }
    }


    

    public void resetPieCount(){
        colorSensor1.resetPieCount();
        colorSensor2.resetPieCount();
    }

    public String verifiedColor(){
        if (colorSensor1.getVerifiedColor(2) == colorSensor2.getVerifiedColor(2)){
            return colorSensor1.verifiedColorChar;
        }
        else{
            return "?";
        }
            

    /**
     * Are both color sensors on the right color?
     *  
     */    
    }
    public boolean targetColorVerified(){
        if ((verifiedColorChar1.equals(targetColor)) && (verifiedColorChar2.equals(targetColor))){
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * Are we within the minimum but under the maximum amount of rotations?
     * 
     */
    public boolean rotationVerified() {
        if ((pieAmount1 >= 24) && (pieAmount1 <= 40) && (pieAmount2 >= 24) && (pieAmount2 <= 40)){
            return true;
        }
        else if  ((pieAmount1 > 40) && (pieAmount2 > 40)) {
            colorSensor1.resetPieCount();
            colorSensor2.resetPieCount();
            return false;
        }
        else{
            return false;
        }
        
    } 

    // Read the game data and translate the color to the color that is 90 degrees offset
    // The function will return R, G, B or Y, if there is good game data, otherwise it returns '?'.
    public String colorToLocate() {
        char targetColor;
        gameData =  DriverStation.getInstance().getGameSpecificMessage();
        if(gameData.length() > 0) {
            switch (gameData.charAt(0)) {
                case 'B' :
                    targetColor = 'R';
                    break;
                case 'G' :
                    targetColor = 'Y';
                    break;
                case 'R' :
                    targetColor = 'B';
                    break;
                case 'Y' :
                    targetColor = 'G';
                    break;
                default :
                    targetColor = '?';
                    break;
            }
        } else {
            targetColor = '?';
        }
        return  ""+targetColor;
    }

    //this initiates START_COUNTING if not already counting
    public void startCounting(){
        if (state != States.COUNTING){
            state = States.START_COUNTING;
        } 
    }

    public void startFinding(){
        if (state != States.FINDING){
            state = States.START_FINDING;
        }
    }
    
    public void update(){
        colorSensor1.update();
        colorSensor2.update();
        verifiedColorChar1 = colorSensor1.getVerifiedColor(2);
        verifiedColorChar2 = colorSensor2.getVerifiedColor(2);
        pieAmount1 = colorSensor1.getPieCount(2);
        pieAmount2 = colorSensor2.getPieCount(2);
        targetColor = this.colorToLocate();

        
        switch (state) {
            case IDLE :
                setMotor(0);
                break;
            case START_COUNTING :
                resetPieCount();
                setMotor(ROTATION_POWER);
                state = States.COUNTING;
                break;
            case START_FINDING :
                targetColor = colorToLocate();
                setMotor(POSITION_POWER);
                state = States.FINDING;
                break;
            case COUNTING :
                if (rotationVerified()){
                    state = States.IDLE;
                }
                break;
            case FINDING :
                if (targetColorVerified()){
                state = States.IDLE;
                }
                break;
            default :
                ;
                break;
        }
    }
    
    
}