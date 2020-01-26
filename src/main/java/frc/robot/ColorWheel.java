package frc.robot;

import edu.wpi.first.wpilibj.util.Color;
//import jdk.nashorn.internal.runtime.regexp.joni.constants.TargetInfo;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Spark;


class ColorWheel{

    ColorSensor colorSensor1;
    ColorSensor colorSensor2;
    String verifiedColorChar1, verifiedColorChar2, targetColor1, targetColor2;; 
    double pieAmount1, pieAmount2;
    

    public ColorWheel(Port one, Port two) {
        //make the sensor variables
        colorSensor1 = new ColorSensor(one);
        colorSensor2 = new ColorSensor(two);
        Spark motor = new Spark(1);
        //private static final Spark frontL = new Spark(Constants.DRIVE_FL), frontR = new Spark(Constants.DRIVE_FR),
			//backL = new Spark(Constants.DRIVE_BL), backR = new Spark(Constants.DRIVE_BR);

        

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
    SmartDashboard.putString("S1-targetColor", ""+targetColor1); 
    SmartDashboard.putString("S2-targetColor", ""+targetColor2); 
    SmartDashboard.putBoolean("targetColorVerified", targetColorVerified());
    SmartDashboard.putBoolean("rotationVerified", rotationVerified());

    

    }


    public void update(){
        colorSensor1.update();
        colorSensor2.update();
        verifiedColorChar1 = colorSensor1.getVerifiedColor(2);
        verifiedColorChar2 = colorSensor2.getVerifiedColor(2);
        pieAmount1 = colorSensor1.getPieCount(2);
        pieAmount2 = colorSensor2.getPieCount(2);
        targetColor1 = colorSensor1.colorToLocate();
        targetColor2 = colorSensor2.colorToLocate();

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
            

        
    }
    public boolean targetColorVerified(){
        if ((verifiedColorChar1.equals(targetColor1)) && (verifiedColorChar2.equals(targetColor2))){
            return true;
        }
        else{
            return false;
        }
    }
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


}