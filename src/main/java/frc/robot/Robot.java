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
  public DriveTrain dt = new DriveTrain();
  Xbox driver = new Xbox(0);
  Xbox operator =  new Xbox(1);
  Intake intake = new Intake();
  Timer timer = new Timer();
  Shooter shooter = new Shooter();

<<<<<<< HEAD
=======
  

>>>>>>> d11d29b57b771f2f216b7424833138e62d64a91c
  
  // class - name - = - new class
  ColorWheel colorWheel  = new ColorWheel(I2C.Port.kOnboard, I2C.Port.kMXP); 

  @Override
  public void robotInit() {
    

  }


  @Override
  public void robotPeriodic() {

  
    
    
    Common.dashBool("time passed", timer.hasPeriodPassed(5));
  }


  @Override
  public void autonomousInit() {
  }

  @Override
  public void autonomousPeriodic() {
    
    //debug();
  }

  @Override
  public void teleopInit() {
    dt.teleopDrive(0, 0);
  }

  @Override
  public void teleopPeriodic() {
    if (driver.when(Xbox.buttons.x)) {
      dt.driveDistance(100);
    } else if (driver.when(Xbox.buttons.a)) {
      dt.driveDistance(-100);
    } else if (driver.when(Xbox.buttons.b)) {
      dt.turn(180);
    } else if (driver.when(Xbox.buttons.y)) {
      dt.turn(0);
    }

    if (driver.when(Xbox.buttons.start)) {
      dt.heading.setAngle(10);
    }

    dt.update();
  }


  @Override
  public void testPeriodic() {
  
    //debug();
  }
  
/***
  private void debug() {
    colorWheel.debug();
  }
  */
} 