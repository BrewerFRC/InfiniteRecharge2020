package frc.robot;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Spark;

/**
 * A class to control the dual elevator climber
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Brent Roberts
 */
public class Climber{
    private Spark leftClimb, rightClimb;
    private Solenoid leftRatchet, rightRatchet;
    private boolean ratchetEngaged = false;
    
    private final double RATCHET_POWER = 0.3;


    public Climber() {
        leftClimb = new Spark(Constants.PWM_LEFT_CLIMBER);
        rightClimb = new Spark(Constants.PWM_RIGHT_CLIMBER);
        rightClimb.setInverted(true);

        leftRatchet = new Solenoid(Constants.SOL_LEFT_RACHET);
        rightRatchet = new Solenoid(Constants.SOL_RIGHT_RACHET);
    }

    /**
     * Engages or disengages the rat
     * 
     * @param engaged Whether to lock or unlock the ratchet 
     */
    public void setRatchet(boolean engaged) {
        leftRatchet.set(engaged);
        rightRatchet.set(engaged);
        ratchetEngaged = engaged;
    }
    /**
     * Gets if the the ratchet is engaged.
     * 
     * @return if the ratchet is engaged.
     */
    public boolean getRatchetEngaged() {
        return ratchetEngaged;
    }

    /**
     * Sets the power to the left side elevator.
     * If the ratchet is engaged then the power is limited against the ratchet.
     * 
     * @param power The power from -1.0 to 1.0 to be set.
     */
    public void setLeftPower(double power) {
        if (ratchetEngaged && power > RATCHET_POWER ) {
            leftClimb.set(RATCHET_POWER);
        } else {
            leftClimb.set(power);
        }
    }

    /**
     * Sets the power to the right side elevator.
     * If the ratchet is engaged then the power is limited against the ratchet.
     * 
     * @param power The power from -1.0 to 1.0 to be set.
     */
    public void setRightPower(double power) {
        if (ratchetEngaged && power > RATCHET_POWER ) {
            rightClimb.set(RATCHET_POWER);
        } else {
            rightClimb.set(power);
        }
    }
}