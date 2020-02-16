package frc.robot;

import frc.robot.Flywheel.Distance;

/**
 * A class to control the robot during the autonomous period.
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Brent Roberts
 */
public class Auto {
    private DriveTrain dt;
    private Shooter shooter;


    public enum autoPaths {
        SHOOT_FROM_ANYWHERE,
        LAYUP,
        TRENCH,
        TRENCH_SHOOT,
        GENERATOR_PICKUP;
    }

    private autoPaths autoPath;

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
        T_SHOOT_DRIVE,
        T_ALIGN,
        T_FIRST_FIRE,
        T_RETURN_TO_HEADING,
        T_TRENCH_RUN,
        T_READY_TO_FIRE,
        T_FINAL_FIRE,
        T_COMPLETE,

        //Generator Pickup(GP)
        GP_INIT,
        GP_DRIVE,
        GP_ALIGN,
        GP_FIRE,
        GP_COMPLETE;
    }

    //Distances
    public final static double OFF_LINE_DIST = 84, //Distance to drive FORWARD before shooting
    WALL_DIST = -120, //Distance to wall from starting point
    SPIN_UP_DIST = -24, //Distance to spin up from wall
    T_SHOOT_DIST = 24, //Distance to move forward to shoot for trench
    TRENCH_RUN_DIST = 195-24, //Length to run into trench
    GP_DRIVE_DIST = 84; //Length to move to generator in inches

    //Angles
    public final static double T_FIRST_SHOOT_ANGLE = 0, //Angle of first trench shoot
    T_TRENCH_ANGLE = 360-25, //Angle to run down the trench, probably zero might want to set it based on start?
    T_FINAL_SHOOT_ANGLE = 360-12, //Final shoot angle of trench
    GP_TURN = 19; //turn angle to shoot was 20

    //Times
    public final static long GP_PICKUP_TIME = 50;

    private long autoTime;

    private autoStates autoState;

    public Auto(DriveTrain dt, Shooter shooter) {
        this.dt = dt;
        this.shooter = shooter;
        setAutoPath(autoPaths.SHOOT_FROM_ANYWHERE);
    }

    public void setAutoPath(autoPaths path) {
        autoPath = path;
        switch (autoPath) {
            case SHOOT_FROM_ANYWHERE:
                autoState = autoStates.GP_INIT;
                break;
            case LAYUP:
                autoState = autoStates.LAY_INIT;
                break;
            case TRENCH:
                autoState = autoStates.T_INIT;
                break;
            case TRENCH_SHOOT:
                autoState = autoStates.T_INIT;
                break;
            case GENERATOR_PICKUP:
                autoState = autoStates.GP_INIT;
                break;
        }
    }

    public void update() {
        switch (autoPath) {
            case SHOOT_FROM_ANYWHERE:
                shootFromAnywhere();
                break;
            case LAYUP:
                layup();
                break;
            case TRENCH:
                trenchRun(false);
                break;
            case TRENCH_SHOOT:
                trenchRun(true);
                break;
            case GENERATOR_PICKUP:
                generatorPickup();
                break;
        }
    }

    public autoStates getState() {
        return autoState;
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
                    shooter.prepFire(Distance.MEDIUM);
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
                    shooter.prepFire(Distance.SHORT);
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

    /**
     * A function to function to run the trench run path
     * 
     * @param shoot Whether or not to shoot at the end of the path.
     */
    public void trenchRun(boolean shoot) {
        switch (autoState) {
            case T_INIT:
                dt.driveDistance(T_SHOOT_DIST);
                autoState = autoStates.T_SHOOT_DRIVE;
                break;
            case T_SHOOT_DRIVE:
                if (dt.driveComplete()) {
                    shooter.prepFire(Distance.MEDIUM);
                    dt.turn(T_FIRST_SHOOT_ANGLE);
                    autoState = autoStates.T_ALIGN;
                }
                break;
            case T_FIRST_FIRE:
                shooter.fireBall();
                if (shooter.empty()) {
                    dt.turn(T_TRENCH_ANGLE);
                    autoState = autoStates.T_RETURN_TO_HEADING;
                }
                break;
            case T_RETURN_TO_HEADING:
                if(dt.driveComplete()) {
                    shooter.intakeOn();
                    dt.driveDistance(TRENCH_RUN_DIST);
                    autoState = autoStates.T_TRENCH_RUN;
                }
                break;
            case T_TRENCH_RUN:
                if (dt.driveComplete()) {
                    if (shoot) {
                        shooter.prepFire(Distance.LONG);//not sure if long or medium
                        dt.turn(T_FINAL_SHOOT_ANGLE);
                        autoState = autoStates.T_READY_TO_FIRE;
                    } else {
                        autoState = autoStates.T_COMPLETE;
                    }
                }
                break;
            case T_READY_TO_FIRE:
                if (shooter.readyToFire() && dt.driveComplete()) {
                    autoState = autoStates.T_FINAL_FIRE;
                }
                break;
            case T_FINAL_FIRE:
                shooter.fireBall();
                if (shooter.empty()) {
                    autoState = autoStates.T_COMPLETE;
                }
                break;
            case T_COMPLETE:
                dt.hold();
                break;
        }
    }

    /**
     * A function to pick up and shoot from the generator.
     */
    public void generatorPickup() {
        switch (autoState) {
            case GP_INIT:
                shooter.toggleIntake();
                dt.driveToWall(this.GP_DRIVE_DIST);
                autoState = autoState.GP_DRIVE;
                break;
            case GP_DRIVE:
                if (dt.driveComplete()) {
                    dt.turn(GP_TURN);
                    shooter.prepFire(Distance.MEDIUM);
                    //shooter.toggleIntake();
                    autoTime = Common.time()+ GP_PICKUP_TIME;
                    autoState = autoState.GP_ALIGN;
                }
                break;
            case GP_ALIGN:
                if (dt.driveComplete() && shooter.readyToFire() && autoTime >= Common.time()) {
                    shooter.fireBall();
                    autoState = autoState.GP_FIRE;
                }
                break;
            case GP_FIRE:
                if (shooter.empty()) {
                    autoState = autoState.GP_COMPLETE;
                } else {
                    shooter.fireBall();
                }
                break;
            case GP_COMPLETE:
                dt.hold();
                break;
        }
    }
}
