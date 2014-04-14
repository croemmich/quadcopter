package com.logicalgrape.quadcopter.input;

public interface Accelerometer {

    /**
     * @return the x value in m/s^2
     */
    public double getX();

    /**
     * @return the y value in m/s^2
     */
    public double getY();

    /**
     * @return the z value in m/s^2
     */
    public double getZ();

}
