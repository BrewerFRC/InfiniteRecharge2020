package frc.robot;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.DigitalInput;

/**
 * A class to control the dual elevator climber
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Brent Roberts
 */
public class Climber{
    Servo leftRatchet = new Servo(Constants.PWM_LEFT_RACHET);
    Servo rightRatchet = new Servo(Constants.PWM_RIGHT_RACHET);

    Spark leftClimber = new Spark(Constants.PWM_LEFT_CLIMBER);
    Spark rightClimber = new Spark(Constants.PWM_RIGHT_CLIMBER);

    DigitalInput leftLimit = new DigitalInput(Constants.DIO_LEFT_CLIMBER);
    DigitalInput rightLimit = new DigitalInput(Constants.DIO_RIGHT_CLIMBER);

    private double LEFTSERVOANGLE = 180;
    private double RIGHTSERVOANGLE = 0;
    private double power = 0;
    private double targetPowerL = 0;
    private double targetPowerR = 0;
    private double HOLDINGPOWER = -.2;
    private boolean locked = false; // is ratchet locking enabled
    private boolean teleop = false; // is the climber under the control of the drivers


    public void update() {
        //auto lock ratchet when amperage crosses threshold
        if (Robot.getPDP().getCurrent(Constants.LEFT_CLIMBER_PDP) > 5 || Robot.getPDP().getCurrent(Constants.RIGHT_CLIMBER_PDP) > 5)  {
            locked = true;
        } 
        if (locked) {
            lock();
        } else {
            unlock();
        }
        //powers the motors
        if (teleop) {
            setLeftPower(targetPowerL);
            setRightPower(targetPowerR);
        } else {
            setLeftPower(HOLDINGPOWER);
            setRightPower(HOLDINGPOWER);
        }
    }

    public void enableTeleop() {
        teleop = true;
    }

    public void unlock() {
        leftRatchet.setAngle(180);
        rightRatchet.setAngle(0);
        locked = false;
    }

    public void lock() {
        leftRatchet.setAngle(90);
        rightRatchet.setAngle(90);
        locked = true;
    }

    public boolean isLocked() {
            return locked;
    }

    public boolean atLeftLimit() {
        return !leftLimit.get();
    }

    public boolean atRightLimit() {
        return !rightLimit.get();
    }
    //inverts for the sake of joystick controls are inverted standard
    public void leftPower(double power) {
        targetPowerL = -power;
    }
    //inverts for the sake of joystick controls are inverted standard
    public void rightPower(double power) {
        targetPowerR = -power;
    }

    private void setLeftPower(double power) {
        if (power < 0) {
            if (atLeftLimit()) {
                Common.debug("is holding power");
                power = HOLDINGPOWER;
            }
        } else if (isLocked()) {
            power = 0;

        }
        leftClimber.set(power);
    }

    private void setRightPower(double power) {
        if (power < 0) {
            if (atRightLimit()) {
            power = HOLDINGPOWER;
            }
        } else if (isLocked()) {
            power = 0;
        }
        rightClimber.set(-power);
    }

    public void debug() {
        Common.dashBool("CLMB: teleop mode", teleop);
        Common.dashBool("CLMB: locked", locked);
        Common.dashNum("CLMB: left draw", Robot.getPDP().getCurrent(Constants.LEFT_CLIMBER_PDP));
    }
}