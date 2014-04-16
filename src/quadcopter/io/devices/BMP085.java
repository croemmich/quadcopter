package quadcopter.io.devices;

import quadcopter.io.Altimeter;
import quadcopter.io.Barometer;
import quadcopter.io.Thermometer;

import java.io.IOException;

public class  BMP085 implements Barometer, Thermometer, Altimeter {

    public static final int ADDRESS = 119;

    @Override
    public double getAltitude() {
        return 0;
    }

    @Override
    public double getPressure() {
        return 0;
    }

    @Override
    public double getTemperature() {
        return 0;
    }

    @Override
    public void start() throws IOException {

    }

    @Override
    public void stop() throws IOException {

    }
}
