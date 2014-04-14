package com.logicalgrape.quadcopter.input;

public class BMP085 implements Barometer, Thermometer, Altimeter {

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

}
