package frc.robot;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import frc.robot.Flywheel.Distance;

public class Shooter {
    private Magazine mag = new Magazine();
    private Intake intake = new Intake();
    private Flywheel flywheel = new Flywheel();
    LED led = new LED();
    
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
        //led.blink(2, 2);  
    }
    /**
     * Prepares shooter for throwing. Provide distance either 'long', 'medium' or 'short'.
     * sets the intake to up and motors off
     * sets the magazine to load breach
     * sets the flywheel to spin up
     */
    public void prepFire(Distance distance) {
        if (mag.isIdle() || !mag.isEmpty() || mag.isJammed()){
            //led.chasing(0, 0, 255, 2);
            intake.stopIntake();
            mag.loadBreach();
            flywheel.start(distance);
        } 
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
            //led.chasingBackwards(0, 0, 255, 2);
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
            //led.solid(255,255,255);
        }
    }

    /**
     * sets the intake to up
     * sets the magazine to eject
     * sets the flywheel to idle
     */
    public void eject() {
        if ((mag.isShootBall() == false) && (mag.isEmpty() == false)) {
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
     * use whenPressed not WhilePressed for this because you dont want to call it 50 times a second
     */
    public void toggleIntake() {
        if (intake.isIdle()) {
            if (mag.breachingStates()) {
                mag.unloadBreach();
                flywheel.stop();
                Common.debug("SH: toggleIntake unloading breach");

            } else if (mag.isReadyToIntake()) {
                intake.startIntake();
                //led.fade(120, 2);
                flywheel.stop();
                Common.debug("SH: toggleIntake starting intake");
            }
        } else {
            intake.stopIntake();
            Common.debug("SH: toggleIntake stoping intake");
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

    public void shooterLED() {
        if (mag.isJammed()) {
            //led.updateBlink(255, 0, 0);
        }
    }
}