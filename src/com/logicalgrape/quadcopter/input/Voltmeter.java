package com.logicalgrape.quadcopter.input;

public interface Voltmeter {

    /**
     * @return the voltage in volts
     */
    public double getVoltage();


    /**
     * @return the current in milliamps
     */
    public double getCurrent();

}
