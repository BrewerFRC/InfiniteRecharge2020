package frc.robot;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;

/**
 * Represents an Xbox controller interface.
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Evan McCoy
 * @author Jacob Cote
 * @author Brent Roberts
 */
public class Xbox extends XboxController {
	private Map<String, Supplier<Boolean>> functionMap = new HashMap<String, Supplier<Boolean>>();
	private Map<String, Boolean> whenMap = new HashMap<String, Boolean>();
	private Map<String, Boolean> fallingMap = new HashMap<String, Boolean>();
	public enum buttons {
		x, y, a, b, start, back, dPadDown, dPadLeft, dPadRight, dPadUp, leftBumper,
		leftTrigger, rightBumper, rightTrigger, rightThumb, leftThumb 
	}
	
	/**
	 * Instantiates the controller on the specified port.
	 * 
	 * @param port the port of the controller.
	 */
	public Xbox(int port) {
		super(port);
		setupFunctions();
	}
	
	/**
	 * The controllable deadzone for the controller.
	 * 
	 * @param input the value of the interface in use.
	 * @param deadzone the absolute value of the deadzone.
	 * @return the input value with deadzone applied.
	 */
	public double deadzone(double input, double deadzone) {
		if (Math.abs(input) < deadzone) {
			return(0);
		} else {
			return(input);
		}
	}
	
	/**
	 * The universal deadzone for the controller.
	 * Uses a deadzone of .2.
	 * 
	 * @param input the value of the interface in use.
	 * @return the input value with deadzone applied.
	 */
	public double deadzone(double input) {
		return deadzone(input, 0.2);
	}
	
	/**
	 * Gets the value of the right trigger with deadzone.
	 * 
	 * @return the value of the right trigger from the deadzone to 1.0.
	 */
	public double getRightTrigger() {
		return deadzone(getTriggerAxis(GenericHID.Hand.kRight));
	}
	
	/**
	 * Gets the value of the left trigger with deadzone.
	 * 
	 * @return the value of the left trigger from the deadzone to 1.0.
	 */
	public double getLeftTrigger() {
		return deadzone(getTriggerAxis(GenericHID.Hand.kLeft));
	}
	
	/**
	 * Returns whether or not the specified button is pressed.
	 * 
	 * @param button the button to check.
	 * @return whether or not the button is pressed.
	 */
	public boolean getPressed(buttons button) {
		if (functionMap.containsKey(button.toString())) {
			return functionMap.get(button.toString()).get();
		}
		return false;
	}
	
	/**
	 * Returns the rising edge of a button press.
	 * 
	 * @param button the button to check rising edge for.
	 * @return whether or not a rising edge was detected.
	 */
	public boolean when(buttons button) {
		//TODO: Debounce buttons
		if (!whenMap.containsKey(button.toString())) {
			return false;
		}
		
		if (getPressed(button)) {
			if (!whenMap.get(button.toString())) {
				whenMap.put(button.toString(), true);
				return true;
			}
		}
		else {
			whenMap.put(button.toString(), false);
		}
		return false;
	}
	
	/**
	 * Returns the falling edge of an button.
	 * 
	 * @param button the button to check the falling edge for.
	 * @return whether or not an falling edge was detected.
	 */
	public boolean falling(buttons button) {
		if (!fallingMap.containsKey(button)) {
			Common.debug("falling map does not contain "+button.toString());
			return false;
		}
		if (fallingMap.get(button.toString())) {
			if (!getPressed(button)) {
				fallingMap.put(button.toString(), false);
				return true;
				}
			else {
				fallingMap.put(button.toString(), true);
				return false;
				}
			}
		else {
			if (getPressed(button)) {
				fallingMap.put(button.toString(), true);
				return false;
			} else {
				fallingMap.put(button.toString(), false);
				return false;
			}
		}
	}
	
	/**
	 * Maps superclass button functions to strings and sets up built-in deadzones.
	 */
	private void setupFunctions() {
		//Changed to private because I didn't see an reason to be public -Brent 10/11/18
		functionMap.put(buttons.a.toString(), this::getAButton);
		whenMap.put(buttons.a.toString(), false);
		fallingMap.put(buttons.a.toString(), false);
		
		functionMap.put(buttons.b.toString(), this::getBButton);
		whenMap.put(buttons.b.toString(), false);
		fallingMap.put(buttons.b.toString(), false);
		
		functionMap.put(buttons.x.toString(), this::getXButton);
		whenMap.put(buttons.x.toString(), false);
		fallingMap.put(buttons.x.toString(), false);
		
		functionMap.put(buttons.y.toString(), this::getYButton);
		whenMap.put(buttons.y.toString(), false);
		fallingMap.put(buttons.y.toString(), false);
		
		functionMap.put(buttons.start.toString(), this::getStartButton);
		whenMap.put(buttons.start.toString(), false);
		fallingMap.put(buttons.start.toString(), false);
		
		functionMap.put(buttons.back.toString(), this::getBackButton);
		whenMap.put(buttons.back.toString(), false);
		fallingMap.put(buttons.back.toString(), false);
		
		functionMap.put(buttons.dPadUp.toString(), () -> {
			return (this.getPOV() == -1) ? false : Math.abs(0 - this.getPOV()) < 45 || Math.abs(360 - this.getPOV()) < 45;
		});
		whenMap.put(buttons.dPadUp.toString(), false);
		fallingMap.put(buttons.dPadUp.toString(), false);
		
		functionMap.put(buttons.dPadRight.toString(), () -> {
			return (this.getPOV() == -1) ? false : Math.abs(90 - this.getPOV()) < 45;
		});
		whenMap.put(buttons.dPadRight.toString(), false);
		fallingMap.put(buttons.dPadRight.toString(), false);
		
		functionMap.put(buttons.dPadDown.toString(), () -> {
			return (this.getPOV() == -1) ? false : Math.abs(180 - this.getPOV()) < 45;
		});
		whenMap.put(buttons.dPadDown.toString(), false);
		fallingMap.put(buttons.dPadDown.toString(), false);
		
		functionMap.put(buttons.dPadLeft.toString(), () -> {
			return (this.getPOV() == -1) ? false : Math.abs(270 - this.getPOV()) < 45;
		});
		whenMap.put(buttons.dPadLeft.toString(), false);
		fallingMap.put(buttons.dPadLeft.toString(), false);
		
		functionMap.put(buttons.leftBumper.toString(), () -> {
			return this.getBumper(GenericHID.Hand.kLeft);
		});
		whenMap.put(buttons.leftBumper.toString(), false);
		fallingMap.put(buttons.leftBumper.toString(), false);
		
		functionMap.put(buttons.rightBumper.toString(), () -> {
			return this.getBumper(GenericHID.Hand.kRight);
		});
		whenMap.put(buttons.rightBumper.toString(), false);
		fallingMap.put(buttons.rightBumper.toString(), false);
		
		functionMap.put(buttons.leftTrigger.toString(), () -> {
			return deadzone(this.getLeftTrigger()) > 0;
		});
		whenMap.put(buttons.leftTrigger.toString(), false);
		fallingMap.put(buttons.leftTrigger.toString(), false);
		
		functionMap.put(buttons.rightTrigger.toString(), () -> {
			return deadzone(this.getRightTrigger()) > 0;
		});
		whenMap.put(buttons.rightTrigger.toString(), false);
		fallingMap.put(buttons.rightTrigger.toString(), false);
		
		functionMap.put(buttons.rightThumb.toString(), () -> {
			return this.getRawButton(10);
		});
		whenMap.put(buttons.rightThumb.toString(), false);
		fallingMap.put(buttons.rightThumb.toString(), false);

		functionMap.put(buttons.leftThumb.toString(), () -> {
			return this.getRawButton(9);
		});
		whenMap.put(buttons.leftThumb.toString(), false);
		fallingMap.put(buttons.leftThumb.toString(), false);
	}
}
