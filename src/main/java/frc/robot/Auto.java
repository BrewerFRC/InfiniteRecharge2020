package frc.robot;


/**
 * A class to control the robot during the autonomous period.
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Brent Roberts
 */
public class Auto {
    private DriveTrain dt;
    private Shooter shooter;


    public enum autoStates {
        //Shoot from anywhere(SFA)
        SFA_INIT,
        SFA_BACK_UP,
        SFA_READY_FIRE,
        SFA_FIRE,
        SFA_COMPLETE;

        //Layup(LAY)

        //Trench(T)

        //Trench Shot(TS)

        //Generator Pickup(GP)

        
    }

    public final static double OFF_LINE_DIST = 0; //Distance to drive back before shooting
    

    private autoStates autoState;

    public Auto(DriveTrain dt, Shooter shooter) {
        this.dt = dt;
        this.shooter = shooter;
    }


    public void update() {

    }

    /**
     * A function to run the shot from anywhere auto process
     */
    public void shootFromAnywhere() {
        switch (autoState) {
            case SFA_INIT:
                dt.driveDistance(this.OFF_LINE_DIST);
                autoState = autoState.SFA_BACK_UP;
            break;
            case SFA_BACK_UP:
                if (dt.driveComplete()) {
                    shooter.prepFire("medium");
                    autoState = autoStates.SFA_READY_FIRE;
                }
            break;
            case SFA_READY_FIRE:
                if (shooter.readyToFire()) {
                    autoState = autoStates.SFA_FIRE;
                }
            break;
            case SFA_FIRE:
                shooter.fireBall();
                if (shooter.empty()) {
                    dt.hold();
                    autoState = autoState.SFA_COMPLETE;
                }
            break;
            case SFA_COMPLETE:

            break;
        }
    }













}