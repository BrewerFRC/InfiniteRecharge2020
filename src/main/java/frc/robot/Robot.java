package frc.robot;

import com.revrobotics.ColorSensorV3;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.XboxController.Button;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

class Robot extends TimedRobot {
  
  // class - name - = - new class
  XboxController a = new XboxController(0);
  ColorWheel colorWheel  = new ColorWheel(I2C.Port.kOnboard, I2C.Port.kMXP); 

  @Override
  public void robotInit() {
    
  }


  @Override
  public void robotPeriodic() {

    colorWheel.update();
    
    
  }


  @Override
  public void autonomousInit() {
  }

  @Override
  public void autonomousPeriodic() {

  }


  @Override
  public void teleopPeriodic() {
    
    if (a.getBButton() == true){
      colorWheel.resetPieCount();
     
    }
  }


  @Override
  public void testPeriodic() {
  
  }
  

  
    

}