package frc.robot;

import com.revrobotics.ColorSensorV3;

import org.opencv.core.RotatedRect;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
//import jdk.nashorn.internal.runtime.regexp.joni.constants.TargetInfo;


class ColorSensor {
    ColorSensorV3 colorSensor;
    boolean blueStoreR, redStoreR, greenStoreR, yellowStoreR;
    Color detectedColor;
    String colorChar, oldColor, verifiedColorChar, oldVerifiedChar = "?", pieCountColor, oldPieCountColor;
    double detectedColorBlue, detectedColorRed, detectedColorGreen, detectedColorYellow, index = 0, count = 0, pieAmount;
    double threshold1 = 0.05; //0.095;//22,55,75
  
  
public ColorSensor(I2C.Port port) {
    this.colorSensor = new ColorSensorV3(port);
     
}



    public String getColor(double r, double b, double g, double threshold) {
      /*
      The real official values
        double blueR = 0.12; 
        double blueB = 0.44;
        double blueG = 0.42;
        double greenR = 0.175; 
        double greenB = 0.25;
        double greenG = 0.56;
        double redR = 0.50; 
        double redB = 0.14;
        double redG = 0.35;
        double yellowR = 0.32; 
        double yellowB = 0.12;
        double yellowG = 0.55;
        */
        // new colour values for testing
        double blueR = 0.16; 
        double blueB = .33;//0.38;
        double blueG = .50;//0.45;
        double greenR = 0.21; 
        double greenB = 0.26;
        double greenG = 0.53;
        double redR = 0.41; 
        double redB = 0.18;
        double redG = 0.4;
        double yellowR = 0.34; 
        double yellowB = 0.12;
        double yellowG = 0.54;
    
        if ((Math.abs(blueR-r) < threshold) && (Math.abs(blueB-b) < threshold) && (Math.abs(blueG-g) < threshold) == true){
          return "B";
        } else if ((Math.abs(redR-r) < threshold) && (Math.abs(redB-b) < threshold) && (Math.abs(redG-g) < threshold) == true){
          return "R";
        } else if ((Math.abs(greenR-r) < threshold) && (Math.abs(greenB-b) < threshold) && (Math.abs(greenG-g) < threshold) == true){
          return "G";
        } else if ((Math.abs(yellowR-r) < threshold) && (Math.abs(yellowB-b) < threshold) && (Math.abs(yellowG-g) < threshold) == true){
          return "Y"; 
        } else {
          return "?";
        }
    
    
      }
    

      public String getVerifiedColor(double numberOfCounts){
        colorChar = getColor(detectedColorRed, detectedColorBlue, detectedColorGreen, threshold1);
        if (oldColor == colorChar) {
          index++;
        } 
        else {
         index = 1;
       }
        if (index >= numberOfCounts) {
          //if (oldVerifiedChar != colorChar) {
           // System.out.println(colorChar);
         //}
          oldVerifiedChar = colorChar;
       }
        oldColor = colorChar;
        return oldVerifiedChar;
      }

    
      public double getPieCount(double numberOfCounts){
        pieCountColor = getVerifiedColor(numberOfCounts); 
        if (!(pieCountColor.equals("?")) && !(pieCountColor.equals(oldPieCountColor))){
          count++; //count = count + 1; 
        }
        
      
        if (!(pieCountColor.equals("?"))){
          oldPieCountColor = pieCountColor;
        } 
        
        
        return count;
      }


      public void resetPieCount(){
        count = 0;
      }

    void update() {
        detectedColor = this.colorSensor.getColor();
        detectedColorBlue = detectedColor.blue;
        detectedColorRed = detectedColor.red;
        detectedColorGreen = detectedColor.green; 
        // method
        colorChar = getColor(detectedColorRed, detectedColorBlue, detectedColorGreen, threshold1);
        verifiedColorChar = getVerifiedColor(2);
        pieAmount = getPieCount(2);
        
    
    }
}