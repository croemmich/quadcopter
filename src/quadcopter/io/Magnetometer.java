package quadcopter.io;

public interface Magnetometer {

    /**
     * @return the heading from 0-360. North=0, S=180
     */
    public double getHeading();

    /**
     * @return the x value in microTesla
     */
    public double getX();

    /**
     * @return the y value in microTesla
     */
    public double getY();

    /**
     * @return the z value in microTesla
     */
    public double getZ();

}
