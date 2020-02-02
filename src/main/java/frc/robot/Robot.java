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
    /* SAMS CODE FOR CONTROL SCHEME
    double drive = -driver.deadzone(driver.getY(GenericHID.Hand.kLeft));
    double turn = -driver.deadzone(driver.getX(Hand.kLeft));
    colorWheel.update();
    shooter.update();

    //DRIVER
    if (driver.getPressed(Xbox.buttons.a)) {
      shooter.toggleIntake();
    }
    if (driver.getPressed(Xbox.buttons.b)) {
      shooter.prepFire("long");
    }
    if (driver.getPressed(Xbox.buttons.y)) {
      shooter.prepFire("short");
    }
    if (driver.getPressed(Xbox.buttons.x)) {
      shooter.prepFire("medium");
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

    //OPERATOR
    if (operator.getPressed(Xbox.buttons.a)) {
      shooter.toggleIntake();
    }
    if (operator.getPressed(Xbox.buttons.dPadUp)) {
      colorWheel.startCounting();
    }
    if (operator.getPressed(Xbox.buttons.dPadDown)) {
      colorWheel.startFinding();
    }
    if (driver.getPressed(Xbox.buttons.start)) {
      shooter.eject();
    }
    */

    //OLD TELEOP CODE FOLLOWS
    //dt.accelDrive(driver.deadzone(driver.getX(GenericHID.Hand.kLeft)), driver.deadzone(driver.getY(Hand.kRight)));
    
    
    
    /*if (a.getBButton() == true){
      colorWheel.resetPieCount();
     
    }*/
    double drive = -driver.deadzone(driver.getY(GenericHID.Hand.kLeft));
    double turn = -driver.deadzone(driver.getX(Hand.kLeft));
    //dt.accelDrive(drive, turn);
    //Common.dashNum("Back Neo", dt.backL.get());
    //Common.dashNum("drive", drive);
    //Common.dashNum("turn", turn);
   // debug();

    //*** INTAKE

    if (driver.getPressed(Xbox.buttons.a)){
      intake.startIntake();
    }
      if (driver.getPressed(Xbox.buttons.b)) {
      intake.stopIntake();
    }
    intake.update();


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
    //Common.dashNum("Back Neo", dt.backL.get());
    //debug();
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