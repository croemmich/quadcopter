package quadcopter.io;

public interface Altimeter extends Device {

    /**
     * @return the altitude in meters
     */
    public double getAltitude();

}
