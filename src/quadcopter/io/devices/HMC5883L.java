package quadcopter.io.devices;

import quadcopter.io.Magnetometer;
import quadcopter.io.i2c.I2CBus;
import quadcopter.io.i2c.I2CDevice;

import javax.measure.Measure;
import javax.measure.VectorMeasure;
import javax.measure.quantity.MagneticFluxDensity;
import javax.measure.unit.NonSI;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class HMC5883L implements Magnetometer {

    public static final int ADDRESS = 30;
    public static final String DEVICE_ID = "H43";

    public static final int REG_CONFIG_A = 0x00;
    public static final int REG_CONFIG_B = 0x01;
    public static final int REG_MODE = 0x02;
    public static final int REG_DATA_X_MSB = 0x03;
    public static final int REG_DATA_Z_MSB = 0x05;
    public static final int REG_DATA_Y_MSB = 0x07;
    public static final int REG_ID_A = 0x0A;

    public static final byte MODE_IDLE = 0b10;
    public static final byte MODE_CONTINOUS = 0b00;

    private final I2CDevice device;
    private double resolution;

    public HMC5883L(I2CBus bus) throws IOException {
        this(bus, ADDRESS);
    }

    public HMC5883L(I2CBus bus, int address) throws IOException {
        this.device = bus.getDevice(address);
    }

    private String readDeviceId() throws IOException {
        byte[] data = new byte[3];
        device.read(REG_ID_A, data, 0, 3);

        return String.valueOf(((char) data[0])) + String.valueOf(((char) data[1])) + String.valueOf(((char) data[2]));
    }

    public void checkDevice() throws IOException {
        if (!DEVICE_ID.equals(readDeviceId())) {
            throw new IOException("Cannot find BMP085 at address " + Integer.toHexString(device.getAddress()));
        }
    }

    @Override
    public void start() throws IOException {
        checkDevice();

        // 8 sample averaging, data rate 30HZ, normal biasing
        device.write(REG_CONFIG_A, (byte) 0b01110100);

        // set range = 2.5 Ga, 1.52 resolution
        device.write(REG_CONFIG_B, (byte) 0b01100000);
        resolution = 1.52;

        // set mode
        device.write(REG_MODE, MODE_CONTINOUS);

        try {
            Thread.sleep(6);
        } catch (InterruptedException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public void stop() throws IOException {
        device.write(REG_MODE, MODE_IDLE);
    }

    public VectorMeasure<MagneticFluxDensity> readVector() throws IOException {
        byte[] data = new byte[6];
        device.read(REG_DATA_X_MSB, data, 0, 6);

        ByteBuffer bb = ByteBuffer.allocate(data.length);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.put(data);

        double x = bb.getShort(0) * resolution;
        double z = bb.getShort(2) * resolution;
        double y = bb.getShort(4) * resolution;

        return VectorMeasure.valueOf(x, y, z, NonSI.GAUSS);
    }

    public Measure<Double, MagneticFluxDensity> readX() throws IOException {
        return read2D(REG_DATA_X_MSB);
    }

    public Measure<Double, MagneticFluxDensity> readY() throws IOException {
        return read2D(REG_DATA_Y_MSB);
    }

    public Measure<Double, MagneticFluxDensity> readZ() throws IOException {
        return read2D(REG_DATA_Z_MSB);
    }

    private Measure<Double, MagneticFluxDensity> read2D(int register) throws IOException {
        byte[] data = new byte[2];
        device.read(register, data, 0, 2);

        ByteBuffer bb = ByteBuffer.allocate(data.length);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.put(data);

        double value = bb.getShort(0) * resolution;

        return Measure.valueOf(value, NonSI.GAUSS);
    }

}
