package frc.robot;

public class Shooter {
    Magazine mag = new Magazine();
    Intake intake = new Intake();
    Flywheel flywheel = new Flywheel();
    
    public void init() {
    }
    
    public void debug() {
    }
    /**
     * sets the intake to up and off
     * sets the magazine to load breach
     * sets the flywheel to spin up
     */
    public void prepFire() {
        intake.stopIntake();
        mag.loadBreach();
        //flywheel.spinUp(distance); {i dont have actal function names this is a placeholder}
    }

    /**
     * sets the intake to up and off
     * sets the magazine to ready to load
     * sets the flywheel to idle
     */
    public void slowFlyWheel() {
        intake.stopIntake();
        mag.unloadBreach();
        //flywheel.stop(); {i dont have actal function names this is a placeholder}
    }

    /**
     * sets the intake to off and up
     * sets the magazine to shoot ball
     * sets the flywheel to ready to throw
     */
    public void shootBall() {
        intake.stopIntake();
        mag.shootBall();
        //flywheel.ready to load {i dont have actal function names this is a placeholder}
    }

    /**
     * sets the intake to up
     * sets the magazine to eject
     * sets the flywheel to idle
     */
    public void eject() {
    }

    /**
     * sets the intake to down and on
     * sets the magazine to idle
     * sets the flywheel to off
     */
    public void loadBall() {
    }
}