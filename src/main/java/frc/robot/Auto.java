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
        SFA_COMPLETE,

        //Layup(LAY)
        LAY_INIT,
        LAY_WAIT_FOR_SPIN,
        LAY_COMPLETE_DRIVE,
        LAY_FIRE,
        LAY_COMPLETE,

        //Trench(T)
        T_INIT,
        T_SPACE_DRIVE,
        T_ALIGN,
        T_FIRST_FIRE,
        T_RETURN_TO_HEADING,
        T_TRENCH_RUN,
        T_READY_TO_FIRE,
        T_FIRE,
        T_COMPLETE,

        //Generator Pickup(GP)

        GP_COMPLETE;
    }

    public final static double OFF_LINE_DIST = 0, //Distance to drive back before shooting
    WALL_DIST = 0, //Distance to wall from starting point
    SPIN_UP_DIST = 0; //Distance to spin up from wall
    

    private autoStates autoState;

    public Auto(DriveTrain dt, Shooter shooter) {
        this.dt = dt;
        this.shooter = shooter;
    }


    public void update() {

    }

    /**
     * A function to run the shot from anywhere auto path
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
                dt.hold();
                break;
        }
    }

    /**
     * A function to run the layup auto proccess that drives to the wall and shoots.
     */
    public void layup() {
        switch(autoState) {
            case LAY_INIT:
                dt.driveToWall(WALL_DIST);
                autoState = autoStates.LAY_WAIT_FOR_SPIN;
                break;
            case LAY_WAIT_FOR_SPIN:
                if (dt.getAverageDist() <= SPIN_UP_DIST) {
                    shooter.prepFire("short");
                    autoState = autoStates.LAY_COMPLETE_DRIVE;
                }
                break;
            case LAY_COMPLETE_DRIVE:
                if (dt.driveComplete()) {
                    autoState = autoStates.LAY_FIRE;
                }
                break;
            case LAY_FIRE:
                shooter.fireBall();
                if (shooter.empty()) {
                    autoState = autoStates.LAY_COMPLETE;
                }
                break;
            case LAY_COMPLETE:
                dt.hold();
                break;
        }
    }













}