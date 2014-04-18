package quadcopter.io;

import java.io.IOException;

public interface Altimeter extends Device {

    /**
     * @return the altitude in meters
     */
    public javax.measure.Measure<Double, javax.measure.quantity.Length> readAltitude() throws IOException;

}
