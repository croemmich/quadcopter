package quadcopter.io;

import java.io.IOException;

public interface Barometer extends Device {

    /**
     * @return the pressure in hPa
     */
    public double readPressure() throws IOException;

}
