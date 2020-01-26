package frc.robot;

/**
 * PID controlled movement to a target position, using PID-controlled velocity as a driving measure.
 * The class can be used much like a normal PID, but with two inputs: {@link PositionByVelocityPID#calc(double, double)}
 * 
 * PID terms can be set with: {@link PositionByVelocityPID#setPositionScalars(double, double, double)} and {@link PositionByVelocityPID#setVelocityScalars(double, double, double)}
 * 
 * Output ranges can be set in the constructor, or with these three functions:
 * - {@link PositionByVelocityPID#setMotorPowerRange(double, double)}
 * - {@link PositionByVelocityPID#setVelocityRange(double, double)}
 * - {@link PositionByVelocityPID#setPositionRange(double, double)}
 * 
 * Update target position with {@link PositionByVelocityPID#setTargetPosition(double)}
 * 
 * Target velocity is automatically controlled by the position PID, or can be manually overridden with {@link PositionByVelocityPID#setTargetVelocity(double)}
 * If manually testing velocity alone, use {@link PositionByVelocityPID#calcVelocity(double)} rather than the standard calc method.
 * 
 * Created February 2018
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Evan McCoy
 */
public class PositionByVelocityPID {
	private PID position;
	private PID velocity;
	private double minimumPosition = 0, maximumPosition = 0;
	
	public PositionByVelocityPID(double minPosition, double maxPosition, double minVelocity, double maxVelocity, double minPower, double maxPower, double minPowerMagnitude, String name) {
		position = new PID(0, 0, 0, false, false, name + "Position");
		position.setOutputLimits(minVelocity, maxVelocity);
		velocity = new PID(0, 0, 0, false, true, name + "Velocity");
		velocity.setOutputLimits(minPower, maxPower);
		velocity.setMinMagnitude(minPowerMagnitude);
		minimumPosition = minPosition;
		maximumPosition = maxPosition;
		correctTargetPosition();
	}
	
	public PositionByVelocityPID(double minPosition, double maxPosition, double minVelocity, double maxVelocity, double minPowerMagnitude, String name) {
		this(minPosition, maxPosition, minVelocity, maxVelocity, -1.0, 1.0, minPowerMagnitude, name);
	}
	
	/**
	 * Reset cumulative values in the position PID.
	 */
	public void resetPositionPID() {
		position.reset();
	}
	
	/**
	 * Reset cumulative values in the velocity PID.
	 */
	public void resetVelocityPID() {
		velocity.reset();
	}
	
	/**
	 * Reset both the position and velocity PIDs.
	 */
	public void reset() {
		position.reset();
		velocity.reset();
	}
	
	/**
	 * Pull term coefficients from the SmartDashboard.
	 */
	public void update() {
		position.update();
		velocity.update();
	}
	
	/**
	 * Sets the inverted state of the position PID.
	 * 
	 * @param inverted is inverted
	 */
	public void setPositionInverted(boolean inverted) {
		position.setInverted(inverted);
	}
	
	/**
	 * Sets the inverted state of the velocity PID.
	 * 
	 * @param inverted is inverted
	 */
	public void setVelocityInverted(boolean inverted) {
		velocity.setInverted(inverted);
	}
	
	/**
	 * Set the term coefficients for the position PID.
	 * 
	 * @param p - proportional scalar
	 * @param i - integral scalar
	 * @param d - derivative scalar
	 */
	public void setPositionScalars(double p, double i, double d) {
		position.setP(p);
		position.setI(i);
		position.setD(d);
		position.postCoefficients();
	}
	
	/**
	 * Set the term coefficients for the velocity PID.
	 * 
	 * @param p - proportional scalar
	 * @param i - integral scalar
	 * @param d - derivative scalar
	 */
	public void setVelocityScalars(double p, double i, double d) {
		velocity.setP(p);
		velocity.setI(i);
		velocity.setD(d);
		velocity.postCoefficients();
	}
	
	/**
	 * Set the target position of the system.
	 * 
	 * @param target the position.
	 */
	public void setTargetPosition(double target) {
		position.setTarget(target);
		correctTargetPosition();
	}
	
	/**
	 * Returns the current target position.
	 * 
	 * @return the position.
	 */
	public double getTargetPosition() {
		return position.getTarget();
	}
	
	/**
	 * Ensures the target position is within the allowed range.
	 */
	private void correctTargetPosition() {
		position.setTarget(
			Math.min(
				maximumPosition, 
				Math.max(
					minimumPosition, 
					position.getTarget()
				)
			)
		);
	}
	
	/**
	 * Set the target velocity of the system.
	 * 
	 * @param target the velocity
	 */
	public void setTargetVelocity(double target) {
		velocity.setTarget(target);
	}
	
	/**
	 * Returns the current target velocity.
	 * 
	 * @return the velocity
	 */
	public double getTargetVelocity() {
		return velocity.getTarget();
	}
	
	/**
	 * Sets a minimum and maximum position for output.
	 * 
	 * @param min - the minimum position
	 * @param max - the maximum position
	 */
	public void setPositionRange(double min, double max) {
		minimumPosition = min;
		maximumPosition = max;
		correctTargetPosition();
	}
	
	/**
	 * Sets a minimum and maximum velocity for output.
	 * 
	 * @param min - the minimum velocity
	 * @param max - the maximum velocity
	 */
	public void setVelocityRange(double min, double max) {
		position.setOutputLimits(min, max);
	}
	
	/**
	 * Sets a minimum and maximum motor power for output.
	 * 
	 * @param min - the minimum motor power
	 * @param max - the maximum motor power
	 */
	public void setMotorPowerRange(double min, double max) {
		velocity.setOutputLimits(min, max);
	}
	
	/**
	 * Sets the minimum magnitude of the velocity PID output.
	 * 
	 * @param minimumAbsoluteValue - the minimum magnitude
	 */
	public void setMotorPowerDeadzone(double minimumAbsoluteValue) {
		velocity.setMinMagnitude(minimumAbsoluteValue);
	}
	
	/**
	 * Runs a position PID calculation given an input position.
	 * 
	 * @param input - the current position of the system.
	 * @return the suggested velocity to reach the target position.
	 */
	public double calcPosition(double input) {
		return position.calc(input);
	}
	
	/**
	 * Runs a velocity PID calculation given an input velocity.
	 * 
	 * @param input - the current velocity of the system.
	 * @return the suggested motor power to reach the target velocity.
	 */
	public double calcVelocity(double input) {
		return velocity.calc(input);
	}
	
	/**
	 * Updates target velocity with a position PID calculation. Runs a velocity PID calculation using updated target.
	 * 
	 * @param position - the current position of the system.
	 * @param velocity - the current velocity of the system.
	 * @return the suggested motor power to reach the target position with controlled velocity.
	 */
	public double calc(double curPosition, double curVelocity) {
		setTargetVelocity(calcPosition(curPosition));
		return calcVelocity(curVelocity);
	}
}
