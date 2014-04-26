package quadcopter.io;

import quadcopter.model.Vector;

import java.io.IOException;

public interface Magnetometer extends Device {

    /**
     * @return a 3D vector of tesla values
     */
    public Vector<Double> readVector() throws IOException;

    /**
     * @return the x value in tesla
     */
    public double readX() throws IOException;

    /**
     * @return the y value in tesla
     */
    public double readY() throws IOException;

    /**
     * @return the z value in tesla
     */
    public double readZ() throws IOException;

}
