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
     * Prepares shooter for throwing. Provide distance either 'long', 'medium' or 'short'.
     * sets the intake to up and motors off
     * sets the magazine to load breach
     * sets the flywheel to spin up
     */
    public void prepFire(String distance) {
        intake.stopIntake();
        mag.loadBreach();
        //flywheel.start(distance); 
    }

    /**
     * Prepare shooter to intake balls
     * sets the intake to up and off
     * sets the magazine to ready to load
     * sets the flywheel to idle
     */
    public void prepLoad() {
        intake.stopIntake();
        mag.unloadBreach();
        //flywheel.stop()
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
     * will chieck if intake is idle and if magazine is in a shooting state
     */
    public void toggleIntake() {
    }
}