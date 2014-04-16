package quadcopter.io;

public interface Voltmeter extends Device {

    /**
     * @return the voltage in volts
     */
    public double getVoltage();


    /**
     * @return the current in milliamps
     */
    public double getCurrent();

}
