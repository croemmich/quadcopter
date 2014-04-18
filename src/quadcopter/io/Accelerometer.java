package quadcopter.io;

import quadcopter.model.Gravity;
import quadcopter.model.Gravity3D;

import java.io.IOException;

public interface Accelerometer extends Device {

    public Gravity readX() throws IOException;

    public Gravity readY() throws IOException;

    public Gravity readZ() throws IOException;

    public Gravity3D read3D() throws IOException;

}
