package quadcopter.io.devices;

import quadcopter.io.Gyroscope;

import java.io.IOException;

public class L3G4200D implements Gyroscope {

    public static final int ADDRESS = 105;

    @Override
    public double getX() {
        return 0;
    }

    @Override
    public double getY() {
        return 0;
    }

    @Override
    public double getZ() {
        return 0;
    }

    @Override
    public void start() throws IOException {

    }

    @Override
    public void stop() throws IOException {

    }
}
