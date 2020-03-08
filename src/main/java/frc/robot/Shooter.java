package frc.robot;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import frc.robot.Flywheel.Distance;

public class Shooter {
    private Magazine mag = new Magazine();
    private Intake intake = new Intake();
    private Flywheel flywheel = new Flywheel();
    private static Shooter instance;

    public Shooter() {
        instance = this;
    }
    
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

    public void init() {
        mag.init();
        intake.init();
        flywheel.init();
    }
    /**
     * Prepares shooter for throwing. Provide distance either 'long', 'medium' or 'short'.
     * sets the intake to up and motors off
     * sets the magazine to load breach
     * sets the flywheel to spin up
     */
    public void prepFire(Distance distance) {
        if (mag.isIdle() || !mag.isEmpty() || !mag.isJammed()){
            intake.stopIntake();
            mag.loadBreach();
            flywheel.start(distance);
        } 
    }

    public void raiseIntake() {
        intake.raiseIntake();
    }

    public static Shooter getInstance() {
        return instance;
    }
    /**
     * 
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
        if (mag.isBreachLoaded() && flywheel.readyToFire()) {
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
        if ((mag.isShootBall() == false) && (mag.isEmpty() == false)) {
            intake.ejectingIntake();
            mag.dumpBalls();
            flywheel.stop();
        }
    }

    /**
     * Sets the intake to start by lowering and spinning or stop it
     */
    public void toggleIntake() {
        /*sets the intake to down and on
        * sets the magazine to idle
        * sets the flywheel to off
        * will chieck if intake is idle and if magazine is in a shooting state
        * use whenPressed not WhilePressed for this because you dont want to call it 50 times a second
        */
        if (intake.isIdle()) {
            if (mag.breachingStates()) {
                mag.unloadBreach();
                flywheel.stop();
                //Common.debug("SH: toggleIntake unloading breach");
            } else if (mag.isReadyToIntake()) {
                intake.startIntake();
                flywheel.stop();
                //Common.debug("SH: toggleIntake starting intake");
            }
        } else {
            intake.stopIntake();
            //Common.debug("SH: toggleIntake stoping intake");
        }
    }
    
    public void intakeOn() {
        intake.startIntake();
    }

    public void intakeOff() {
        intake.stopIntake();
    }

    public void intakeReverse() {
        intake.reverse();
    }

    public void toggleIntakeReverse() {
        if (intake.isReverse()) {
            intake.stopIntake();
        } else {
            intake.reverse();
        }
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
    public boolean shooting() {
        return mag.isShootBall();
    }
    public boolean isLoading() {
        return (intake.isLoading());
    }
}