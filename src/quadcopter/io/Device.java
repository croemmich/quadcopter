package quadcopter.io;

import java.io.IOException;

public interface Device {

    /**
     * Called to power-on or setup a device.
     */
    public void start() throws IOException;

    /**
     * Called to power-down a device.
     */
    public void stop() throws IOException;

}
