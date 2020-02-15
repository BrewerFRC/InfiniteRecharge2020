package frc.robot;

import com.revrobotics.ColorSensorV3;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.XboxController.Button;
import edu.wpi.first.wpilibj.controller.ElevatorFeedforward;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Compressor;

class Robot extends TimedRobot {
  private DriveTrain dt = new DriveTrain();
  private Xbox driver = new Xbox(0);
  private Xbox operator =  new Xbox(1);
  private Climber climber = new Climber();
  private Shooter shooter = new Shooter();
  private Compressor compressor = new Compressor(Constants.PCM_CAN_ID);
  private static PowerDistributionPanel pdp = new PowerDistributionPanel();
  private Auto auto =  new Auto(dt, shooter);

  
  // class - name - = - new class
  ColorWheel colorWheel  = new ColorWheel(I2C.Port.kOnboard, I2C.Port.kMXP); 

  @Override
  public void robotInit() {
    shooter.init();
  }


  @Override
  public void robotPeriodic() {
  }


  @Override
  public void autonomousInit() {

  }

  @Override
  public void autonomousPeriodic() {
    auto.update();
    dt.update();
    shooter.update();
    Common.dashStr("Auto state", auto.getState().toString());
    //debug();
  }

  @Override
  public void teleopInit() {
    compressor.setClosedLoopControl(true);
    //dt.teleopDrive(0, 0);
  }

  @Override
  public void teleopPeriodic() {

    /*if (driver.when(Xbox.buttons.x)) {
      dt.turn(auto.GP_TURN);
      //dt.driveToWall(-60);
    } else if (driver.when(Xbox.buttons.a)) {
      //dt.driveDistance(-100);
    } else if (driver.when(Xbox.buttons.b)) {
      dt.turn(90);
    } else if (driver.when(Xbox.buttons.y)) {
      dt.turn(0);
    }

    if (driver.when(Xbox.buttons.start)) {
      dt.heading.reset();;
    }*/
    
    //SAMS CODE FOR CONTROL SCHEME
    double drive = -driver.deadzone(driver.getY(GenericHID.Hand.kLeft));
    double turn = -driver.deadzone(driver.getX(Hand.kLeft));
    dt.teleopDrive(drive, turn);

    //DRIVER
    if (driver.when(Xbox.buttons.a)) {
      shooter.toggleIntake();
    }
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
    if (driver.when(Xbox.buttons.leftBumper))  {
      dt.shiftLow();
    }
    else if (driver.when(Xbox.buttons.rightBumper))  {
      dt.shiftHigh();
    }
    //driver.deadzone(driver.getX(GenericHID.Hand.kLeft), driver.deadzone(driver.getY(Hand.kRight)));
    if (driver.getPressed(Xbox.buttons.start)) {
      shooter.eject();
    }
    if (driver.getPressed(Xbox.buttons.back)) {
      shooter.prepLoad();
    }

    //OPERATOR
    /*if (operator.when(Xbox.buttons.a)) {
      shooter.toggleIntake();
    }
    if (operator.getPressed(Xbox.buttons.b)) {
      climber.setRatchet(true);
    }
    if (operator.getPressed(Xbox.buttons.x)) {
      climber.setRatchet(false);
    }/*
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
    //climber.setLeftPower(operator.deadzone(operator.getY(GenericHID.Hand.kLeft)));
    //climber.setRightPower(operator.deadzone(operator.getY(GenericHID.Hand.kRight)));

   
    
    
    
    /*if (a.getBButton() == true){
      colorWheel.resetPieCount();
     
    }*/
    
   // debug();

    dt.update();
    colorWheel.update();
    shooter.update();
    shooter.debug();
    colorWheel.debug();
  }

  

  @Override
  public void testInit() {
    compressor.setClosedLoopControl(true);
  }

  @Override
  public void testPeriodic() {
    
    
  }

  @Override
  public void disabledInit() {
    // TODO Auto-generated method stub
    super.disabledInit();
  }

  @Override
  public void disabledPeriodic() {
    if (driver.when(Xbox.buttons.start)) {
      dt.heading.reset();;
    }
  }
  
/***
  private void debug() {
    colorWheel.debug();
  }
  */
  public static PowerDistributionPanel getPDP() {
    return pdp;
  }
} 