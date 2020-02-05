package frc.robot;

import frc.robot.Flywheel.Distance;

public class Shooter {
    private Magazine mag = new Magazine();
    private Intake intake = new Intake();
    private Flywheel flywheel = new Flywheel();
    
    public void update() {
        mag.update();
        intake.update();
        flywheel.update();
        if (mag.fullyLoaded() && intake.isLoading()) {
            intake.stopIntake();
        }
        if (mag.isEmpty()  && flywheel.readyToFire()) {
            flywheel.stop();
        }

    }
    
    public void debug() {
        mag.debug();
        intake.debug();
        flywheel.debug();
    }
    /**
     * Prepares shooter for throwing. Provide distance either 'long', 'medium' or 'short'.
     * sets the intake to up and motors off
     * sets the magazine to load breach
     * sets the flywheel to spin up
     */
    public void prepFire(Distance distance) {
        if (mag.isIdle() || !mag.isEmpty()) {
            intake.stopIntake();
            mag.loadBreach();
            flywheel.start(distance);
        } 
    }

    /**
     * Prepare shooter to intake balls
     * sets the intake to up and off
     * sets the magazine to ready to load
     * sets the flywheel to idle
     */
    public void prepLoad() {
        if (mag.isShootBall() || !mag.isEmpty()) {
            intake.stopIntake();
            mag.unloadBreach();
            flywheel.stop();
        }
    }

    /**
     * sets the intake to off and up
     * sets the magazine to shoot ball
     * sets the flywheel to ready to throw
     */
    public void fireBall() {
        if (mag.isBreachLoaded() && flywheel.atRPM()) {
            intake.stopIntake();
            mag.shootBall();
        }
    }

    /**
     * sets the intake to up
     * sets the magazine to eject
     * sets the flywheel to idle
     */
    public void eject() {
        if (mag.isIdle()) {
            intake.stopIntake();
            mag.dumpBalls();
            flywheel.stop();
        }
    }

    /**
     * sets the intake to down and on
     * sets the magazine to idle
     * sets the flywheel to off
     * will chieck if intake is idle and if magazine is in a shooting state
     */
    public void toggleIntake() {
        if (mag.isIdle() && flywheel.isIdle())
            if (intake.isIdle()) {
                intake.startIntake();
                mag.unloadBreach();
                flywheel.stop();
            } else {
                intake.stopIntake();
                mag.unloadBreach();
                flywheel.stop();
            }
    }
    public void intakeOn() {
        intake.startIntake();
    }
    public boolean readyToLoad() {
        return (mag.isIdle() && flywheel.isIdle());
    }
    public boolean readyToFire() {
        return (mag.isBreachLoaded() && intake.isIdle() && flywheel.readyToFire());
    }
    public boolean empty() {
        return mag.isEmpty();
    }
}