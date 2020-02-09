package frc.robot;

import edu.wpi.first.networktables.*;

/**
 * A class to incoroporate vision from a limelight.
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Brent Roberts
 * @author Samuel Woodward
 */
public class Limelight2 {

    final private String TABLE = "limelight";

    /**
     * A function to return data from limelight
     * 
     * Normal possible entries:
     * tv	Whether the limelight has any valid targets (0 or 1)
     * tx	Horizontal Offset From Crosshair To Target (LL2: -29.8 to 29.8 degrees)
     * ty	Vertical Offset From Crosshair To Target (LL2: -24.85 to 24.85 degrees)
     * ta	Target Area (0% of image to 100% of image)
     * ts	Skew or rotation (-90 degrees to 0 degrees)
     * tl	The pipeline’s latency contribution (ms) Add at least 11ms for image capture latency.
     * tshort	Sidelength of shortest side of the fitted bounding box (pixels)
     * tlong	Sidelength of longest side of the fitted bounding box (pixels)
     * thor	Horizontal sidelength of the rough bounding box (0 - 320 pixels)
     * tvert	Vertical sidelength of the rough bounding box (0  - 320 pixels)
     * getpipe	True active pipeline index of the camera (0 .. 9)
     * camtran	Results of a 3D position solution, 6 numbers: Translation (x,y,z) Rotation(pitch,yaw,roll)
     */
    public double getDouble( String entry) {
        return NetworkTableInstance.getDefault().getTable(TABLE).getEntry(entry).getDouble(0);
    }

    public Boolean hasTarget() {
        //private to limit network table calls
        return getDouble("tv") >= 1.0;
    }

    /**
     * Horizontal offset.
     * From Crosshair To Target (LL2: -29.8 to 29.8 degrees)
     * @return horizontal offset
     */
    public double getHorizOffset() {
        return getDouble("tx");
    }
    /**
     * Vertical offset
     * From Crosshair To Target (LL2: -24.85 to 24.85 degrees)
     * @return
     */
    public double getVertOffset() {
        return getDouble("ty");
    }
    /**
     * Target area
     * (0% of image to 100% of image)
     * @return
     */
    public double getTargetArea() {
        return getDouble("ta");
    }
    /**
     * Skew or rotation
     * (-90 degrees to 0 degrees)
     * @return
     */
    public double getRotation() {
        return getDouble("ts");
    }
    /**
     * Latency
     * The pipeline’s latency contribution (ms) Add at least 11ms for image capture latency.
     * @return
     */
    public double getLatency() {
        return getDouble("tl");
    }
    /**
     * Shortest side
     * shortest side of the fitted bounding box (pixels)
     * @return
     */
    public double getShortestSide() {
        return getDouble("tshort");
    }
    /**
     * Longest side
     * longest side of the fitted bounding box (pixels)
     * @return
     */
    public double getLongestSide() {
        return getDouble("tlong");
    }
    /**
     * Horizontal side length
     * rough bounding box (0 - 320 pixels)
     * @return
     */
    public double gethorzSidelength() {
        return getDouble("thor");
    }
    /**
     * Vertical side length
     * rough bounding box (0  - 320 pixels)
     * @return
     */
    public double getvertSidelength() {
        return getDouble("tvert");
    }
    /**
     * Pipeline
     * True active pipeline index of the camera (0 .. 9)
     * @return
     */
    public double getPipe() {
        return getDouble("getpipe");
    }
    /**
     * 3D position solution
     * 6 numbers: Translation (x,y,z) Rotation(pitch,yaw,roll)
     * @return
     */
    public double getcamTran() {
        return getDouble("camtran");
    }

    private void setControl(String entry, double  value) {
        NetworkTableInstance.getDefault().getTable(TABLE).getEntry(entry).setNumber(value);
    }

    public void setLimelight(boolean On) {
        //set limelight on or off
        if (On) {
            setControl("ledMode", 0); //on
        } else {
            setControl("ledMode", 1);
        }
    }
}