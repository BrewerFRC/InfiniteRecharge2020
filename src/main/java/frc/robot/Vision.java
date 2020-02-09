package frc.robot;

/**
 * A class to control a simple vision system for the 2020 FRC game
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Brent Roberts
 */
public class Vision {
    public Limelight2 ll = new Limelight2();
    private PID visionPID;

    private final double P = 0, I = 0, D = 0, MAX_OUTPUT = .3;
    private final double ACCEPTABLE_ERROR = 1;


    public Vision() {
        visionPID = new PID(P, I, D, true, false, "Vision PID", true);
        visionPID.setOutputLimits(-MAX_OUTPUT, MAX_OUTPUT);
    }

    /**
     * Calculates the current turn component to find the target.
     * 
     * @return The result of a PID with the input of the horizoffset.
     */
    public double calcTurn() {
        if (ll.hasTarget()) {
            return visionPID.calc(ll.getHorizOffset());
        }
        return 0.0;
    }

    /**
     * Calculates the current drive component to find the target.
     * 
     * @return 0.0 as of right now.  
     */
    public double calcDrive() {
        return 0.0;
    }

    /**
     * Gets if the robot is aligned with the target
     * 
     * @return True if the robot is aligned within acceptable error. 
     */
    public boolean getAtTarget() {
        return ll.getHorizOffset() <= ACCEPTABLE_ERROR && ll.getHorizOffset() >= -ACCEPTABLE_ERROR;
    }


}