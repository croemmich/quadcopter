package com.logicalgrape.quadcopter.input;

public interface Gyroscope {

    /**
     * @return the x value in degrees/s
     */
    public double getX();

    /**
     * @return the y value in degrees/s
     */
    public double getY();

    /**
     * @return the z value in degrees/s
     */
    public double getZ();

}
