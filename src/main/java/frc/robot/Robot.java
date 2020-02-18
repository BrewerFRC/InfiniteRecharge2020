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
import frc.robot.Auto.paths;
import frc.robot.Xbox.buttons;
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
    if (driver.getPressed(buttons.leftTrigger)) {
      dt.visionTrack();
      if (dt.vis.getAtTarget()) {
        shooter.fireBall();
      }
    } else {
      dt.teleopDrive(drive, turn);
    }

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
    /*
    if (operator.when(Xbox.buttons.a)) {
      shooter.toggleIntake();
    }*/
    if (operator.getPressed(Xbox.buttons.b)) {
      climber.enableTeleop();
    }
    if (operator.getPressed(Xbox.buttons.x)) {
      climber.lock();
    }
    if (operator.getPressed(Xbox.buttons.y)) {
      climber.unlock();
    }
    /*
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
    climber.leftPower(operator.deadzone(operator.getY(GenericHID.Hand.kLeft)));
    climber.rightPower(operator.deadzone(operator.getY(GenericHID.Hand.kRight)));

   
    
    
    
    /*if (a.getBButton() == true){
      colorWheel.resetPieCount();
     
    }*/
    
   // debug();

    dt.update();
    colorWheel.update();
    shooter.update();
    shooter.debug();
    colorWheel.debug();
    climber.update();
    climber.debug();
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
    dt.vis.ll.setLight(false);  
  }

  @Override
  public void disabledPeriodic() {
    if (driver.when(Xbox.buttons.start)) {
      dt.heading.reset();
    }
    if (driver.when(Xbox.buttons.rightBumper) || operator.when(buttons.rightBumper)) {
      dt.vis.ll.setLight(true);
    } else if (driver.when(buttons.leftBumper) || operator.when(buttons.leftBumper)) {
      dt.vis.ll.setLight(false);
    }

    if (driver.when(buttons.a)) {
      auto.setAutoPath(Auto.paths.SHOOT_FROM_ANYWHERE);
    } else if (driver.when(buttons.b)) {
      auto.setAutoPath(paths.GENERATOR_PICKUP);
    } else if (driver.when(buttons.x)) {
      auto.setAutoPath(paths.LAYUP);
    } else if (driver.when(buttons.y)) {
      auto.setAutoPath(paths.TRENCH);
    } else if (driver.when(buttons.dPadUp)) {
      auto.setAutoPath(paths.TRENCH_SHOOT);
    }

    debug();
  }
  

  private void debug() {
    Common.dashNum("LL horizental offset", dt.vis.ll.getHorizOffset());
    Common.dashBool("LL hasTarget", dt.vis.ll.hasTarget());
    Common.dashBool("LL at Target", dt.vis.getAtTarget());
  }
  public static PowerDistributionPanel getPDP() {
    return pdp;
  }
} 