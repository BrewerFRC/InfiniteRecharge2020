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
    Servo leftRatchet = new Servo(Constants.SOL_LEFT_RACHET);
    Servo rightRatchet = new Servo(Constants.SOL_RIGHT_RACHET);

    Spark leftClimber = new Spark(Constants.PWM_LEFT_CLIMBER);
    Spark rightClimber = new Spark(Constants.PWM_RIGHT_CLIMBER);

    DigitalInput leftLimit = new DigitalInput(Constants.DIO_LEFT_CLIMBER);
    DigitalInput rightLimit = new DigitalInput(Constants.DIO_RIGHT_CLIMBER);

    private double leftServoAngle = 90;
    private double rightServoAngle = 90;
    private double power = 0;
    private double holdingPower = -.1;

    private void lock() {
        leftRatchet.setAngle(leftServoAngle);
        rightRatchet.setAngle(rightServoAngle);
    }

    public boolean isLocked() {
        if (leftRatchet.getAngle() == leftServoAngle && rightRatchet.getAngle() == rightServoAngle) {
            return true;
        } else {
            return false;
        }
    }

    public void leftPower(double power) {
        setLeftPower(power);
    }

    public void rightPower(double power) {
        setRightPower(power);
    }

    private void setLeftPower(double power) {
        if (power < 0) {
            power = holdingPower;
        } else if (isLocked() == false) {
            power = 0;
            
        }
        leftClimber.set(power);
    }

    private void setRightPower(double power) {
        if (power < 0) {
            power = holdingPower;
        } else if (isLocked() == false) {
            power = 0;
        }
        leftClimber.set(power);
    }
}