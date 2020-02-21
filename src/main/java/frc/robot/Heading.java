package frc.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;

/**
 * Heading control utility class for the ADXRS450 Gyro sensor.
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Evan McCoy
 * @author Brent Roberts
 */
public class Heading {
	private static final double P = 0.05, I = 0, D = 0; //was P=0.03
	private static final double MAX_OUTPUT = .5, MIN_OUTPUT = 0;// MIN was .13 MAX was .75
	
	private ADXRS450_Gyro gyro;
	//PID takes cumulative angles
	private PID pid;
	private boolean headingHold;
	
	/**
	 * Initializes heading with the specified PID values.
	 * 
	 * @param p the p scaler.
	 * @param i the integral scaler.
	 * @param d the derivative scaler.
	 */
	public Heading() {
		pid = new PID(P, I, D, true, false, "gyro", false);
		//PID is dealing with error; an error of 0 is always desired.
		pid.setTarget(0.0);
		pid.setMinMagnitude(MIN_OUTPUT);
		pid.setOutputLimits(-MAX_OUTPUT, MAX_OUTPUT);
		gyro = new ADXRS450_Gyro();	
	}
	
	/**
	 * Resets gyro PID and gyro
	 */
	public void reset() {
		gyro.reset();
		resetPID();
	}
	
	public void calibrate() {
		gyro.calibrate();
	}
	
	/**
	 * Resets gyro PID
	 */
	public void resetPID() {
		pid.reset();
	}
	
	/**
	 * Sets new PID values to the gyro PID.
	 * 
	 * @param p the p scaler.
	 * @param i the integral scaler.
	 * @param d the derivative scaler.s
	 */
	public void setPID(double p, double i, double d) {
		pid.setP(p);
		pid.setI(i);
		pid.setD(d);
	}
	
	/*
	 * Returns the targeted angle.
	 * 
	 * @return double the angle in degrees.
	 */
	public double getTargetAngle() {
		return pid.getTarget(); 
	}
	
	/**
	 * Returns the targeted heading.
	 * 
	 * @return double the heading in degrees.
	 */
	public double getTargetHeading(){
		return angleToHeading(pid.getTarget());
	}
	
	/**
	 * Convert angle to heading in partial degrees, 0.01 accuracy
	 * 
	 * @param angle the input angle to convert.
	 * @return double the heading in degrees.
	 */
	public double angleToHeading(double angle) {
		double heading = (angle * 100) % 36000 / 100;
		if (heading < 0) {
			heading += 360;
		}
		return heading; 
	}
	
	/**
	 * Returns current heading.
	 * 
	 * @return double the current heading in degrees.
	 */
	public double getHeading() {
		return angleToHeading(getAngle());
	}
	
	/**
	 * Returns current angle.
	 * 
	 * @return double the current angle in degrees.
	 */
	public double getAngle() {
		return gyro.getAngle();
	}
	
	/**
	 * Sets the PID target to the defined angle.
	 * 
	 * @param angle the target angle
	 */
	public void setAngle(double angle) {
		pid.setTarget(angle);
	}
	
	/**
	 * Sets target angle given a heading, and will turn left or right to target dependent on which is shortest.
	 * 
	 * @param heading the heading to set the target to in degrees.
	 */
	public void setHeading(double heading) {
		//Find the short and long path to the desired heading.
		double changeLeft = (-360 + (heading - getHeading())) % 360;
		double changeRight = (360 - (getHeading() - heading)) % 360;
		double change = (Math.abs(changeLeft) < Math.abs(changeRight)) ? changeLeft : changeRight;
		pid.setTarget(getAngle() + change);
	}
	
	/**
	 * Turn a number of degrees relative to current angle.
	 * 
	 * @param degrees the amount of degrees relative to current angle.
	 */
	public void relTurn(double degrees){
		pid.setTarget(getAngle() + degrees);
	}
	
	/**
	 * Modifies the current target by the defined term.
	 * 
	 * @param degrees the amount of degrees to change the target by.
	 */
	public void incrementTargetAngle(double degrees) {
		pid.setTarget(pid.getTarget() + degrees);
	}
	
	/**
	 * Activates or deactivates heading hold.  If setting heading hold, it will reset the PID and and set target heading to current heading
	 * 
	 * @param headingHold whether or not heading hold should be enabled.
	 */
	public void setHeadingHold(boolean headingHold) {
		if (headingHold) {
			resetPID();
			this.headingHold = true;
			//Set target angle to current heading.
			setHeading(getHeading());
		}
		else {
			resetPID();
			this.headingHold = false;
		}
	}
	
	/**
	 * Returns state of heading hold.
	 * 
	 * @return boolean whether or not heading hold is enabled.
	 */
	public boolean isHeadingHold() {
		return headingHold;
	}
	
	/**
	 * This returns the PID-recommended turn power required to turn to target heading.
	 * 
	 * @return double the PID recommended turn rate.
	 */
	public double turnRate() {
		pid.update();
		double turnRate = pid.calc(gyro.getAngle());
		return turnRate;
	}
	
	/**
	 * Returns the direction, left or right, the robot must move to meet its target.
	 * 
	 * @return the direction, 1 for left, -1 for right
	 */
	public double getDirection() {
		return (getAngle() < pid.getTarget()) ? 1 : -1;
	}
}