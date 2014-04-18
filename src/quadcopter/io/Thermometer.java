package quadcopter.io;

import java.io.IOException;

public interface Thermometer {

    /**
     * @return the temperature in degrees centigrade
     */
    public javax.measure.Measure<Double, javax.measure.quantity.Temperature> readTemperature() throws IOException;

}
