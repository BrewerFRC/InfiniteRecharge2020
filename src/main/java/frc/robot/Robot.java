package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

class Robot extends TimedRobot {
  DriveTrain dt = new DriveTrain();
  Xbox driver = new Xbox(0);
  Xbox operator =  new Xbox(1);

  
  @Override
  public void robotInit() {
  }


  @Override
  public void robotPeriodic() {
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


    
    debug();
  }


  @Override
  public void testPeriodic() {
    debug();
  }

  private void debug() {

  }
}
