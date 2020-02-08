package frc.robot;

/**
 * A class to list constants between other classes.
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Brent Roberts
 * @author Sworp
 */
public class Constants {
    //PWM
    static final int PWM_INTAKE_MOTOR = 0, PWM_COLORWHEEL_MOTOR = 1, PWM_MAGAZINE_MOTOR = 2;

    //DIO
    public static final int DIO_TOP_BEAMBREAK = 0, DIO_BOTTOM_BEAMBREAK = 1;


    //ANA


    //CAN
    

    //Motors
    public static final int DRIVE_FL = 13, DRIVE_ML = 14, DRIVE_BL =15, DRIVE_FR = 11, DRIVE_MR = 10, DRIVE_BR = 12,
    FLYWHEEL_LEFT_CAN_ID = 16, FLYWHEEL_RIGHT_CAN_ID = 17;

    //Devices
    public static final int PCM_CAN_ID = 2;
    
    //Pneumatics - Solenoids
    public static final int SOL_SHIFTER = 0; 
    public static final int SOL_INTAKE_ARM = 1; 
    public static final int SOL_FLAPPER = 2; 
    
    //Miscellaneous
    public static double REFRESH_RATE = 20;
    public static final int MAGAZINE_PDP_PORT = 8;

}