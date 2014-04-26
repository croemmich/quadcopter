package quadcopter.io;

import java.io.IOException;

public interface Thermometer {

    /**
     * @return the temperature in degrees centigrade
     */
    public double readTemperature() throws IOException;

}
