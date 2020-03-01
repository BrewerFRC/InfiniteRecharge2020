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


    public static enum paths {
        SHOOT_FROM_ANYWHERE,
        LAYUP,
        TRENCH,
        TRENCH_SHOOT,
        GENERATOR_PICKUP;
    }

    private paths autoPath;

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
        LAY_WAIT_FOR_SHOT,
        LAY_COMPLETE,

        //Trench(T)
        T_INIT,
        T_SHOOT_DRIVE,
        T_TURN,
        T_ALIGN,
        T_FIRST_FIRE,
        T_RETURN_TO_HEADING,
        T_TRENCH_RUN,
        T_READY_TO_FIRE,
        T_FINAL_FIRE,
        T_COMPLETE,

        //Generator Pickup(GP)
        GP_INIT,
        GP_FAST_DRIVE,
        GP_PICKUP,
        GP_DRIVE,
        GP_TURN,
        GP_ALIGN,
        GP_FIRE,
        GP_2ND_TURN,
        GP_2ND_DRIVE,
        GP_COMPLETE;
    }

    //Distances
    public final static double OFF_LINE_DIST = 84, //Distance to drive FORWARD before shooting
    WALL_DIST = -130, //Distance to wall from starting point
    SPIN_UP_DIST = -24, //Distance to spin up from wall
    T_SHOOT_DIST = 48, //Distance to move forward to shoot for trench
    TRENCH_RUN_DIST = 175-48, //Length to run into trench
    GP_DRIVE_DIST = 93, // was 90 Length to move to generator in inches
    GP_2ND_DRIVE_DISTANCE = 48; // 48 inches ACROSS GENERATOR

    //Angles
    public final static double T_FIRST_SHOOT_ANGLE = 347, //Angle of first trench shoot
    T_TRENCH_ANGLE = 360-25, //Angle to run down the trench, probably zero might want to set it based on start?
    T_FINAL_SHOOT_ANGLE = 360-12, //Final shoot angle of trench
    GP_TURN = 19; //turn angle to shoot was 20

    //Times
    public final static long GP_PICKUP_TIME = 2000;

    private long autoTime;

    private autoStates autoState;

    public Auto(DriveTrain dt, Shooter shooter) {
        this.dt = dt;
        this.shooter = shooter;
        setAutoPath(paths.SHOOT_FROM_ANYWHERE);
    }

    public void setAutoPath(paths path) {
        autoPath = path;
        switch (autoPath) {
            case SHOOT_FROM_ANYWHERE:
                Common.debug("Setting auto to SFA");
                autoState = autoStates.SFA_INIT;
                break;
            case LAYUP:
                Common.debug("setting auto to LAY");
                autoState = autoStates.LAY_INIT;
                break;
            case TRENCH:
                Common.debug("setting auto to TRENCH");
                autoState = autoStates.T_INIT;
                break;
            case TRENCH_SHOOT:
                Common.debug("setting auto to TRENCH SHOOT");
                autoState = autoStates.T_INIT;
                break;
            case GENERATOR_PICKUP:
                Common.debug("setting auto to GP");
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
                if (shooter.shooting()) {
                    autoTime = (Common.time() + 1500);
                    autoState = autoStates.LAY_WAIT_FOR_SHOT;
                }
                break;
            case LAY_WAIT_FOR_SHOT:
                if (Common.time() >= autoTime) {
                    autoState = autoStates.LAY_FIRE;
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
                    autoState = autoStates.T_TURN;
                }
                break;
            case T_TURN:
                if (dt.driveComplete()) {
                    dt.visionTrack();
                    autoState = autoStates.T_ALIGN;
                }
                break;
            case T_ALIGN:
                if (dt.driveComplete()) {
                    autoState = autoStates.T_FIRST_FIRE;
                }
                break;
            case T_FIRST_FIRE:
                shooter.fireBall();
                if (shooter.empty()) {
                    dt.turn(0);
                    autoState = autoStates.T_RETURN_TO_HEADING;
                }
                break;
            case T_RETURN_TO_HEADING:
                if(dt.driveComplete()) {
                    shooter.intakeOn();
                    dt.driveDistance(TRENCH_RUN_DIST, 0.50);
                    autoState = autoStates.T_TRENCH_RUN;
                }
                break;
            case T_TRENCH_RUN:
                if (dt.driveComplete()) {
                    if (shoot) {
                        if (shooter.readyToLoad()) {
                            shooter.intakeOff();
                            shooter.prepFire(Distance.MEDIUM);//not sure if long or medium
                            dt.visionTrack();
                            autoState = autoStates.T_READY_TO_FIRE;
                        }
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
                dt.driveDistance(this.GP_DRIVE_DIST - 20);
                autoTime = Common.time()+ GP_PICKUP_TIME;
                Common.debug("AUTO: GP_FAST_DRIVE");
                autoState = autoState.GP_FAST_DRIVE;
                break;
            case GP_FAST_DRIVE:
                if(dt.driveComplete()) {
                    dt.driveDistance(20, .3);
                    Common.debug("AUTO: GP_PICKUP");
                    autoState = autoStates.GP_PICKUP;
                }
                break;
            case GP_PICKUP:
                if (dt.driveComplete() && (Common.time() >= autoTime)) {
                    //shooter.toggleIntake();
                    dt.driveDistance(-10);
                    Common.debug("AUTO: GP_DRIVE");
                    autoState = autoState.GP_DRIVE;
                }
                break;
            case GP_DRIVE:
                if (dt.driveComplete()) {
                    autoTime = Common.time()+ GP_PICKUP_TIME;
                    dt.turn(GP_TURN);
                    Common.debug("AUTO: GP_TURN");
                    autoState = autoStates.GP_TURN;
                }
                break;
            case GP_TURN:
                if (dt.driveComplete()) {
                    //Common.debug("GP_PAUSE over, autoTime: "+autoTime+" Common.time: "+Common.time());
                    dt.visionTrack();
                    shooter.prepFire(Distance.MEDIUM);
                    Common.debug("AUTO: GP_ALIGN");
                    autoState = autoStates.GP_ALIGN;
                }
                break;
            case GP_ALIGN:
                //Common.dashNum("Auto time", );
                if (dt.vis.getAtTarget() && shooter.readyToFire()) {
                    shooter.fireBall();
                    Common.debug("AUTO: GP_FIRE");
                    autoState = autoState.GP_FIRE;
                }
                break;
            case GP_FIRE:
                if (shooter.empty()) {
                    dt.turn(0);
                    Common.debug("AUTO: GP_2ND_TURN");
                    autoState = autoState.GP_2ND_TURN;
                } else {
                    shooter.fireBall();
                }
                break;
            case GP_2ND_TURN:
                if (dt.driveComplete()){
                    dt.driveDistance(GP_2ND_DRIVE_DISTANCE);
                    Common.debug("AUTO: GP_2ND_DRIVE");
                    autoState = autoStates.GP_2ND_DRIVE;
                }
                break;
            case GP_2ND_DRIVE:
                if (dt.driveComplete()) {
                    Common.debug("AUTO: GP_COMPLETE");
                    autoState = autoStates.GP_COMPLETE;
                }
                break;
            case GP_COMPLETE:
                dt.hold();
                break;
        }
    }
}
