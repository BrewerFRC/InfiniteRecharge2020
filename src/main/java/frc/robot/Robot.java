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
  //public DriveTrain dt = new DriveTrain();
  Xbox driver = new Xbox(0);
  //box operator =  new Xbox(1);
  //Intake intake = new Intake();
  Timer timer = new Timer();
  Shooter shooter = new Shooter();

  

  
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
    //dt.teleopDrive(0, 0);
  }

  @Override
  public void teleopPeriodic() {
    //SAMS CODE FOR CONTROL SCHEME
    double drive = -driver.deadzone(driver.getY(GenericHID.Hand.kLeft));
    double turn = -driver.deadzone(driver.getX(Hand.kLeft));

    //DRIVER
    /*if (driver.getPressed(Xbox.buttons.a)) {
      shooter.toggleIntake();
    }*/
    if (driver.getPressed(Xbox.buttons.b)) {
      shooter.prepFire(Flywheel.Distance.LONG);
    }
    if (driver.getPressed(Xbox.buttons.y)) {
      shooter.prepFire(Flywheel.Distance.SHORT);
    }
    if (driver.getPressed(Xbox.buttons.x)) {
      shooter.prepFire(Flywheel.Distance.MEDIUM);
    }
    if (driver.getPressed(Xbox.buttons.rightTrigger)) {
      shooter.fireBall();
    }
    if (driver.getPressed(Xbox.buttons.leftBumper))  {
      //dt.shiftDown();
    }
    if (driver.getPressed(Xbox.buttons.rightBumper))  {
      //dt.shiftUp();
    }
    //driver.deadzone(driver.getX(GenericHID.Hand.kLeft), driver.deadzone(driver.getY(Hand.kRight)));
    if (driver.getPressed(Xbox.buttons.start)) {
      shooter.eject();
    }
    if (driver.getPressed(Xbox.buttons.back)) {
      shooter.prepLoad();
    }

    //OPERATOR
    /*if (operator.getPressed(Xbox.buttons.a)) {
      shooter.toggleIntake();
    }
    if (operator.getPressed(Xbox.buttons.dPadUp)) {
      colorWheel.startCounting();
    }
    if (operator.getPressed(Xbox.buttons.dPadDown)) {
      colorWheel.startFinding();
    }
    if (operator.getPressed(Xbox.buttons.start)) {
      shooter.eject();
    }
    if (operator.getPressed(Xbox.buttons.back)) {
      shooter.prepLoad();
    }*/
    

    //OLD TELEOP CODE FOLLOWS
    //dt.accelDrive(driver.deadzone(driver.getX(GenericHID.Hand.kLeft)), driver.deadzone(driver.getY(Hand.kRight)));
    
    
    
    /*if (a.getBButton() == true){
      colorWheel.resetPieCount();
     
    }*/
    
   // debug();

    //dt.update();
    colorWheel.update();
    shooter.update();
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