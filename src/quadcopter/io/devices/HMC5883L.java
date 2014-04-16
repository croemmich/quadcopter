package quadcopter.io.devices;

import quadcopter.io.Magnetometer;

public class HMC5883L implements Magnetometer {

    public static final int ADDRESS = 30;

    public double getHeading() {
        return 0;
    }

    public double getX() {
        return 0;
    }

    public double getY() {
        return 0;
    }

    public double getZ() {
        return 0;
    }

}
