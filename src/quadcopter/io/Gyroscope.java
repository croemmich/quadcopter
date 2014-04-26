package quadcopter.io;

import quadcopter.model.Vector;

import java.io.IOException;

public interface Gyroscope extends Device {

    /**
     * @return a 3D vector of dps values
     * @throws IOException
     */
    public Vector<Double> readVector() throws IOException;

    /**
     * @return the x value in dps
     */
    public double getX();

    /**
     * @return the y value in dps
     */
    public double getY();

    /**
     * @return the z value in dps
     */
    public double getZ();

}
