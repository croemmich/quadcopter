package quadcopter.io;

public interface Barometer extends Device {

    /**
     * @return the pressure in hPa
     */
    public double getPressure();

}
