package frc.robot;

import com.revrobotics.ColorSensorV3;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.XboxController.Button;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Timer;

class Robot extends TimedRobot {
  DriveTrain dt = new DriveTrain();
  Xbox driver = new Xbox(0);
  Xbox operator =  new Xbox(1);

  
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
    
    debug();
  }


  @Override
  public void teleopPeriodic() {
    
    if (a.getBButton() == true){
      colorWheel.resetPieCount();
     
    }
    dt.accelDrive(driver.deadzone(driver.getX(GenericHID.Hand.kLeft)), driver.deadzone(driver.getY(Hand.kRight)));
    debug();
  }


  @Override
  public void testPeriodic() {
  
    debug();
  }

  private void debug() {

  }
  

  
    

}