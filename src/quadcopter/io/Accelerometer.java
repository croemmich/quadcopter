package quadcopter.io;

import quadcopter.model.Vector;

import java.io.IOException;

public interface Accelerometer extends Device {

    public double readX() throws IOException;

    public double readY() throws IOException;

    public double readZ() throws IOException;

    public Vector<Double> readVector() throws IOException;

}
