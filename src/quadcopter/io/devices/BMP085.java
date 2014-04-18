package quadcopter.io.devices;

import quadcopter.Contstants;
import quadcopter.io.Altimeter;
import quadcopter.io.Barometer;
import quadcopter.io.Thermometer;
import quadcopter.io.i2c.I2CBus;
import quadcopter.io.i2c.I2CDevice;

import javax.measure.Measure;
import javax.measure.quantity.Length;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.Temperature;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static javax.measure.unit.SI.*;

public class BMP085 implements Barometer, Thermometer, Altimeter {

    public static final int ADDRESS = 119;
    public static final int DEVICE_ID = 0x55;

    public static final int REG_DEVICE_ID = 0xD0;
    public static final int REG_AC1 = 0xAA;
    public static final int REG_CTRL = 0xF4;
    public static final int REG_DATA = 0xF6;
    public static final byte CMD_TEMP = 0x2E;
    public static final byte CMD_PRESSURE = (byte) 0x34;

    public static final byte MODE_LOW = 0;
    public static final byte MODE_STD = 1;
    public static final byte MODE_HIGH = 2;
    public static final byte MODE_ULTRA = 3;

    private short cal_ac1;
    private short cal_ac2;
    private short cal_ac3;
    private int cal_ac4; //unsigned short
    private int cal_ac5; //unsigned short
    private int cal_ac6; //unsigned short
    private short cal_b1;
    private short cal_b2;
    private short cal_mb;
    private short cal_mc;
    private short cal_md;

    private final I2CDevice device;
    private byte mode = MODE_ULTRA;
    private short cacheUT;
    private long cacheUTExpires = 0;

    public BMP085(I2CBus bus) throws IOException {
        this(bus, ADDRESS);
    }

    public BMP085(I2CBus bus, int address) throws IOException {
        this.device = bus.getDevice(address);
    }

    public int readDeviceId() throws IOException {
        return device.read(REG_DEVICE_ID);
    }

    public void checkDevice() throws IOException {
        if (readDeviceId() != DEVICE_ID) {
            throw new IOException("Cannot find BMP085 at address " + Integer.toHexString(device.getAddress()));
        }
    }

    @Override
    public void start() throws IOException {
        checkDevice();
        loadCalibration();
    }

    @Override
    public void stop() throws IOException {

    }

    @Override
    public Measure<Double, Length> readAltitude() throws IOException {
        Measure<Double, Pressure> pressure = readPressure();
        double temperature = readCompensatedTemperature(false);

        double altitude = ((Math.pow(Contstants.PRESSURE_SEALEVEL / (pressure.getValue() / 100), 0.190223) - 1.0) * (temperature + 273.15)) / 0.0065;

        return Measure.valueOf(altitude, METER);
    }

    @Override
    public Measure<Double, Pressure> readPressure() throws IOException {
        int up = readUncompensatedPressure();
        short ut = readUncompensatedTemperature(false);

        // calculate temp
        long x1 = ((ut - cal_ac6) * cal_ac5) >> 15;
        long x2 = (cal_mc << 11) / (x1 + cal_md);
        long b5 = x1 + x2;

        // calculate pressure
        long b6 = b5 - 4000;
        x1 = (cal_b2 * ((b6 * b6) >> 12)) >> 11;
        x2 = (cal_ac2 * b6) >> 11;
        long x3 = x1 + x2;
        long b3 = (((cal_ac1 * 4 + x3) << mode) + 2) / 4;

        x1 = (cal_ac3 * b6) >> 13;
        x2 = (cal_b1 * ((b6 * b6) >> 12)) >> 16;
        x3 = ((x1 + x2) + 2) >> 2;
        long b4 = (cal_ac4 * ((x3 + 32768))) >> 15;
        long b7 = (up - b3) * (50000 >> mode);

        long pressure;
        if (b7 < 0x80000000) {
            pressure = (b7 << 1) / b4;
        } else {
            pressure = (b7 / b4) << 1;
        }

        x1 = (pressure >> 8) * (pressure >> 8);
        x1 = (x1 * 3038) >> 16;
        x2 = (-7357 * pressure) >> 16;
        pressure += ((x1 + x2 + 3791) >> 4);

        return Measure.valueOf((double) pressure, PASCAL);
    }

    @Override
    public Measure<Double, Temperature> readTemperature() throws IOException {
        return Measure.valueOf(readCompensatedTemperature(true), CELSIUS);
    }

    private double readCompensatedTemperature(boolean fresh) throws IOException {
        short ut = readUncompensatedTemperature(fresh);
        int x1 = ((ut - cal_ac6) * cal_ac5) >> 15;
        int x2 = (cal_mc << 11) / (x1 + cal_md);
        int b5 = x1 + x2;
        int t = (b5 + 8) / 16;
        return t / 10;
    }

    private void loadCalibration() throws IOException {
        byte[] data = new byte[22];
        device.read(REG_AC1, data, 0, 22);

        ByteBuffer bb = ByteBuffer.allocate(data.length);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.put(data);

        cal_ac1 = bb.getShort(0);
        cal_ac2 = bb.getShort(2);
        cal_ac3 = bb.getShort(4);
        cal_ac4 = bb.getShort(6) & 0xffff;
        cal_ac5 = bb.getShort(8) & 0xffff;
        cal_ac6 = bb.getShort(10) & 0xffff;

        cal_b1 = bb.getShort(12);
        cal_b2 = bb.getShort(14);
        cal_mb = bb.getShort(16);
        cal_mc = bb.getShort(18);
        cal_md = bb.getShort(20);
    }

    private short readUncompensatedTemperature(boolean fresh) throws IOException {
        if (fresh || cacheUTExpires < System.currentTimeMillis()) {
            System.out.println("Updating Temp Cache");
            device.write(REG_CTRL, CMD_TEMP);

            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                throw new IOException(e.getMessage(), e);
            }

            byte[] data = new byte[2];
            device.read(REG_DATA, data, 0, 2);

            ByteBuffer bb = ByteBuffer.allocate(data.length);
            bb.order(ByteOrder.BIG_ENDIAN);
            bb.put(data);

            cacheUT = bb.getShort(0);
            cacheUTExpires = System.currentTimeMillis() + 5000;
        }

        return cacheUT;
    }

    private int readUncompensatedPressure() throws IOException {
        device.write(REG_CTRL, (byte) (CMD_PRESSURE + (mode << 6)));

        try {
            switch (mode) {
                case MODE_LOW:
                    Thread.sleep(5);
                    break;
                case MODE_STD:
                    Thread.sleep(8);
                    break;
                case MODE_HIGH:
                    Thread.sleep(14);
                    break;
                case MODE_ULTRA:
                    Thread.sleep(26);
                    break;
            }
        } catch (InterruptedException e) {
            throw new IOException(e.getMessage(), e);
        }

        byte[] data = new byte[3];
        device.read(0xF6, data, 0, 3);

        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.put((byte) 0x00);
        bb.put(data);

        return bb.getInt(0) >> (8 - mode);
    }

}
