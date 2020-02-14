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
    
    private final double RATCHET_POWER = 0.3, RATCHET_AMPS = 100;


    public Climber() {
        leftClimb = new Spark(Constants.PWM_LEFT_CLIMBER);
        rightClimb = new Spark(Constants.PWM_RIGHT_CLIMBER);

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

    public void update() {
        /*if (Robot.getPDP().getCurrent(Constants.LEFT_CLIMBER_PDP) >= RATCHET_AMPS || Robot.getPDP().getCurrent(Constants.RIGHT_CLIMBER_PDP) >= RATCHET_AMPS) {
            setRatchet(true);
        }*/
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
        if (ratchetEngaged && power > 0 ) {
            leftClimb.set(power * RATCHET_POWER);
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
        if (ratchetEngaged && power > 0 ) {
            rightClimb.set(power * RATCHET_POWER);
        } else {
            rightClimb.set(power);
        }
    }
}