package quadcopter.io;

import java.io.IOException;

public interface Barometer extends Device {

    /**
     * @return the pressure in hPa
     */
    public javax.measure.Measure<Double, javax.measure.quantity.Pressure> readPressure() throws IOException;

}
