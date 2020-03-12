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
    static final int PWM_INTAKE_MOTOR = 0, PWM_COLORWHEEL_MOTOR = 1, PWM_MAGAZINE_MOTOR = 2, PWM_LEFT_CLIMBER = 3, PWM_RIGHT_CLIMBER = 4 , PWM_LEFT_RACHET= 5, PWM_RIGHT_RACHET = 6;

    //DIO
    public static final int DIO_TOP_BEAMBREAK = 0, DIO_BOTTOM_BEAMBREAK = 1, DIO_LEFT_CLIMBER = 2, DIO_RIGHT_CLIMBER = 3;


    //ANA
    public static final int ANA_TOP_BEAMBREAK = 0, ANA_BOTTOM_BEAMBREAK = 1;
    //CAN
    

    //Motors
    public static final int DRIVE_FL = 13, DRIVE_ML = 14, DRIVE_BL =15, DRIVE_FR = 11, DRIVE_MR = 10, DRIVE_BR = 12,
    FLYWHEEL_LEFT_CAN_ID = 16, FLYWHEEL_RIGHT_CAN_ID = 17;

    //Devices
    public static final int PCM_CAN_ID = 1;
    
    //Pneumatics - Solenoids
    public static final int SOL_SHIFTER = 0, SOL_INTAKE_OUT_ARM = 1, SOL_INTAKE_IN_ARM = 2, SOL_FLAPPER = 3;  
    
    //Miscellaneous
    public static double REFRESH_RATE = 20;
    public static final int MAGAZINE_PDP_PORT = 8, LEFT_CLIMBER_PDP = 4, RIGHT_CLIMBER_PDP = 5;

}