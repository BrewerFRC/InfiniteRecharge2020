package frc.robot;

import com.revrobotics.ColorSensorV3;


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
  public static Robot instance;
  private DriveTrain dt = new DriveTrain();
  private Xbox driver = new Xbox(0);
  private Xbox operator =  new Xbox(1);
  private Climber climber = new Climber();
  private Shooter shooter = new Shooter();
  private ColorWheel colorWheel = new ColorWheel(); 
  private Compressor compressor = new Compressor(Constants.PCM_CAN_ID);
  private static PowerDistributionPanel pdp = new PowerDistributionPanel();
  private Auto auto =  new Auto(dt, shooter);
  private double drive, turn;
  
  @Override
  public void robotInit() {
    shooter.init();
    instance = this;
  }

  @Override
  public void robotPeriodic() {
  }

  @Override
  public void autonomousInit() {
    dt.heading.reset();
  }

  @Override
  public void autonomousPeriodic() {
    auto.update();
    dt.update();
    shooter.update();
    climber.update();
    debug();
  }

  @Override
  public void teleopInit() {
    compressor.setClosedLoopControl(true);
    //dt.teleopDrive(0, 0);
  }

  @Override
  public void teleopPeriodic() {

    if (driver.when(Xbox.buttons.start)) {
      dt.heading.reset();;
    }
    
    //SAM'S CODE FOR CONTROL SCHEME
    // DriveTrain Control
    if (driver.getPressed(buttons.leftTrigger)) {
      dt.visionTrack();
      if (dt.vis.getAtTarget()) {
        shooter.fireBall();
      }
    } else if (operator.getPressed(Xbox.buttons.dPadUp)) {
      dt.teleopDrive(-.2, 0);
      if (operator.when(Xbox.buttons.dPadUp)) {
        colorWheel.startCounting();
      }
    } else if (operator.getPressed(Xbox.buttons.dPadDown)) {
      dt.teleopDrive(-.2, 0);
      if (operator.when(Xbox.buttons.dPadDown)) {
        colorWheel.startFinding();
      }
    } else {
      operator.when(Xbox.buttons.dPadDown);
      operator.when(Xbox.buttons.dPadUp);
      drive = -driver.deadzone(driver.getY(GenericHID.Hand.kLeft));
      turn = -driver.deadzone(driver.getX(Hand.kLeft));
      if (drive==0 && turn==0) {
        drive = operator.deadzone(operator.getY(GenericHID.Hand.kLeft));
        turn = -operator.deadzone(operator.getX(Hand.kLeft));  
      }
      dt.teleopDrive(drive, turn * 0.75);
    }

    //DRIVER
    if (driver.when(Xbox.buttons.a)) {
      shooter.toggleIntake();
    }
    if (driver.getPressed(Xbox.buttons.b)) {
      shooter.raiseIntake();
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
    if (driver.getPressed(Xbox.buttons.start)) {
      Common.debug("ROBO: Eject");
      shooter.eject();
    }
    if (driver.getPressed(Xbox.buttons.back)) {
      Common.debug("ROBO: Prep Load");
      shooter.prepLoad();
    }

    //OPERATOR
    if (operator.getPressed(Xbox.buttons.b)) {
      climber.enableTeleop();
    }
    if (operator.getPressed(Xbox.buttons.x)) {
      climber.lock();
    }
    if (operator.getPressed(Xbox.buttons.y)) {
      climber.unlock();
    }
    
    
    if (operator.getPressed(Xbox.buttons.start)) {
      shooter.eject();
    }
    if (operator.getPressed(Xbox.buttons.back)) {
      shooter.prepLoad();
    }

    climber.teleopControl(operator.deadzone(operator.getX(GenericHID.Hand.kRight)),
                          operator.deadzone(operator.getY(GenericHID.Hand.kRight)));

    /*if (a.getBButton() == true){
      colorWheel.resetPieCount();
    }*/
    
    dt.update();
    colorWheel.update();
    shooter.update();
    climber.update();
    debug();
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
    if (driver.getPressed(Xbox.buttons.rightBumper) || operator.getPressed(buttons.rightBumper)) {
      dt.vis.ll.setLight(true);
    } else {//if (driver.when(buttons.leftBumper) || operator.when(buttons.leftBumper)) {
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
    Common.dashNum("LL: horizental offset", dt.vis.ll.getHorizOffset());
    Common.dashBool("LL: hasTarget", dt.vis.ll.hasTarget());
    Common.dashBool("LL: at Target", dt.vis.getAtTarget());
    Common.dashStr("Auto: state", auto.getState().toString());
    shooter.debug();
    colorWheel.debug();
  }
  public static PowerDistributionPanel getPDP() {
    return pdp;
  }

  public static Robot instance() {
		return instance;
	}
}