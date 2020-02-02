package frc.robot;

public class Shooter {
    private Magazine mag = new Magazine();
    private Intake intake = new Intake();
    private Flywheel flywheel = new Flywheel();
    
    public void update() {
        mag.update();
        intake.update();
        //flywheel.update();
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
    public void fireBall() {
        intake.stopIntake();
        mag.shootBall();
        //flywheel.atRPM {i dont have actual function names this is a placeholder}
    }

    /**
     * sets the intake to up
     * sets the magazine to eject
     * sets the flywheel to idle
     */
    public void eject() {
        intake.stopIntake();
        mag.dumpBalls();
        //flywheel.stop();
    }

    /**
     * sets the intake to down and on
     * sets the magazine to idle
     * sets the flywheel to off
     * will chieck if intake is idle and if magazine is in a shooting state
     */
    public void toggleIntake() {
        if (intake.isIdle()) {
            intake.startIntake();
            mag.stop();
            //flyWheel.stop();
        } else {
            intake.stopIntake();
            mag.unloadBreach();
            //flyWheel.stop();
        }
    }
    public boolean readyToLoad() {
        return (mag.isIdle() && flywheel.isIdle());
    }
    public boolean readyToFire() {
        return (mag.readyToFire() && intake.isIdle() && flywheel.readyToFire());
    }
    public boolean empty() {
        return mag.empty();
    }
}