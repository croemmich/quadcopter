package quadcopter.io;

import quadcopter.model.XYZ;

import java.io.IOException;

public interface Accelerometer extends Device {

    /**
     * @return the x value in m/s^2
     */
    public double getX() throws IOException;

    /**
     * @return the y value in m/s^2
     */
    public double getY() throws IOException;

    /**
     * @return the z value in m/s^2
     */
    public double getZ() throws IOException;

    /**
     * @return the zyz in m/s^2
     */
    public XYZ getXYZ() throws IOException;

}
