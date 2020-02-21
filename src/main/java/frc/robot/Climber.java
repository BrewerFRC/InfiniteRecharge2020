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

    private double leftCurrent = 0, rightCurrent = 0;
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
        updateLeftCurrent();
        updateRightCurrent();
        if (getLeftCurrent() > 5 || getRightCurrent() > 5)  {
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
        leftRatchet.setAngle(140);
        rightRatchet.setAngle(0);
        locked = false;
    }

    public void lock() {
        leftRatchet.setAngle(0);
        rightRatchet.setAngle(140); //was 90
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


    // Joystick control for climber.
    public void teleopControl(double x, double y) {
        // negate the y-axis value so that joystick up is a positive value
        y = -y;  
        // If x-axis is within deadzone, then y-axis operates both left and right climbers
        if (Math.abs(x) < 0.75)  {
            setLeftTarget(y);
            setRightTarget(y);
        } else {
            // Retract slowly, based on which way the x-axis is pushed
            if (x > 0) {
                setLeftTarget(0.0);
                setRightTarget(-(x - 0.5));

            } else {
                setLeftTarget((x + 0.5));
                setRightTarget(0.0);
            }
        }
    }

    //Set target power for left climber. Positive power extends climber.
    private void setLeftTarget(double power) {
        targetPowerL = power;
    }
    
    //Set target power for right climber. Positive power extends climber.
    private void setRightTarget(double power) {
        targetPowerR = power;
    }

    private void setLeftPower(double power) {
        if (power < 0) {
            if (atLeftLimit()) {
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

    /**
     * Updates the left current using a complementary filter.
     */
    private void updateLeftCurrent() {
        leftCurrent = leftCurrent * .98 + Robot.getPDP().getCurrent(Constants.LEFT_CLIMBER_PDP) * .02;
    }

    /**
     * Gets the left climber current post complementary filter. 
     * 
     * @return The left climber current post complementary filter in PDP amps.
     */
    public double getLeftCurrent() {
        return leftCurrent;
    }

     /**
     * Updates the left current using a complementary filter.
     */
    private void updateRightCurrent() {
        rightCurrent = rightCurrent * .98 + Robot.getPDP().getCurrent(Constants.RIGHT_CLIMBER_PDP) * .02;
    }

    /**
     * Gets the left climber current post complementary filter. 
     * 
     * @return The left climber current post complementary filter in PDP amps.
     */
    public double getRightCurrent() {
        return rightCurrent;
    }

    public void debug() {
        Common.dashBool("CLMB: teleop mode", teleop);
        Common.dashBool("CLMB: locked", locked);
        Common.dashNum("CLMB: left draw", getLeftCurrent());
        Common.dashNum("CLMB: Left pdp read", Robot.getPDP().getCurrent(Constants.LEFT_CLIMBER_PDP));
    }
}