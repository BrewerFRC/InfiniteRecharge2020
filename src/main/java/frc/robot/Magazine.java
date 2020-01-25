package frc.robot;

import edu.wpi.first.wpilibj.DigitalInput;

public class Magazine {

    private DigitalInput topBeamBreak = new DigitalInput(Constants.DIO_TOP_BEAMBREAK);
    private DigitalInput bottomBeamBreak = new DigitalInput(Constants.DIO_BOTTOM_BEAMBREAK);

    /**
     * 
     */
    public void init(){

    }

    /**
     * 
     */
	public void debug(){
        
    }

	public boolean bottomSensorTriggered(){
        if (bottomBeamBreak.get()) {
            return true;
        } else {
            return false;
        }
    }

	public boolean topSensorTriggered(){
        if (topBeamBreak.get()) {
            return true;
        } else {
            return false;
        }
    }

	public boolean fullyLoaded(){
        if (topSensorTriggered() && bottomSensorTriggered()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 
     */
	public void load(){

    }

    /**
     * 
     */
	public void stop(){

    }

    /**
     * 
     */
	public void unload(){

    }

    /**
     * 
     */
	public void shootBall(){

    }

    /**
     * 
     */
	public void LoadBreach(){

    }
}