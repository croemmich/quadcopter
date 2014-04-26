package quadcopter.io;

import java.io.IOException;

public interface Altimeter extends Device {

    /**
     * @return the altitude in meters
     */
    public double readAltitude() throws IOException;

}
