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
import frc.robot.Xbox.buttons;
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
    if (driver.getPressed(buttons.x)) {
      dt.resetEncoders();
    }

    //colorWheel.update();
    
    
   // Common.dashBool("time passed", timer.hasPeriodPassed(5));
  }

  @Override 
  public void disabledPeriodic() {
    
    debug();
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
    
    /*if (a.getBButton() == true){
      colorWheel.resetPieCount();
     
    }*/
    double drive = -driver.deadzone(driver.getY(GenericHID.Hand.kLeft));
    double turn = -driver.deadzone(driver.getX(Hand.kLeft));
    dt.accelDrive(drive, turn);

    dt.update();
    Common.dashNum("Back Neo", dt.backL.get());
    Common.dashNum("drive", drive);
    Common.dashNum("turn", turn);
    debug();
  }


  @Override
  public void testPeriodic() {
  
    debug();
  }

  private void debug() {
    Common.dashNum("DT Average Distance", dt.getAverageDist());
    Common.dashNum("DT Average Velocity", dt.getAverageVelocity());
    Common.dashNum("DT left distance", dt.getLeftDist());
    Common.dashNum("DT left velocity", dt.getLeftVelocity());
    Common.dashNum("DT right distance", dt.getRightDist());
    Common.dashNum("DT right velocity", dt.getRightVelocity());

  }
}  

  
    

