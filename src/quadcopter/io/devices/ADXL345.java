package quadcopter.io.devices;

import quadcopter.io.Accelerometer;
import quadcopter.io.i2c.I2CBus;
import quadcopter.io.i2c.I2CDevice;

import javax.measure.Measure;
import javax.measure.VectorMeasure;
import javax.measure.quantity.Acceleration;
import javax.measure.unit.NonSI;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ADXL345 implements Accelerometer {

    public static final int ADDRESS = 0x53;
    public static final int DEVICE_ID = 0xE5;

    public static final int REG_DEVICE_ID = 0x0;
    public static final int REG_THRESH_TAP = 0x1D;
    public static final int REG_OFSX = 0x1E;
    public static final int REG_OFSY = 0x1F;
    public static final int REG_OFSZ = 0x20;
    public static final int REG_DUR = 0x21;
    public static final int REG_LATENT = 0x22;
    public static final int REG_WINDOW = 0x23;
    public static final int REG_THRESH_ACT = 0x24;
    public static final int REG_THRESH_INACT = 0x25;
    public static final int REG_TIME_INACT = 0x26;
    public static final int REG_ACT_INACT_CTL = 0x27;
    public static final int REG_THRESH_FF = 0x28;
    public static final int REG_TIME_FF = 0x29;
    public static final int REG_TAP_AXES = 0x2A;
    public static final int REG_ACT_TAP_STATUS = 0x2B;
    public static final int REG_BW_RATE = 0x2C;
    public static final int REG_POWER_CTL = 0x2D;
    public static final int REG_INT_ENABLE = 0x2E;
    public static final int REG_INT_MAP = 0x2F;
    public static final int REG_INT_SOURCE = 0x30;
    public static final int REG_DATA_FORMAT = 0x31;
    public static final int REG_DATAX0 = 0x32;
    public static final int REG_DATAX1 = 0x33;
    public static final int REG_DATAY0 = 0x34;
    public static final int REG_DATAY1 = 0x35;
    public static final int REG_DATAZ0 = 0x36;
    public static final int REG_DATAZ1 = 0x37;
    public static final int REG_FIFO_CTL = 0x38;
    public static final int REG_FIFO_STATUS = 0x39;

    private int CAL_X = 114;
    private int CAL_Y = 159;
    private int CAL_Z = -738;

    private int SENSITIVITY_X = 305;
    private int SENSITIVITY_Y = 296;
    private int SENSITIVITY_Z = 243;

    private final I2CDevice device;

    public ADXL345(I2CBus bus) throws IOException {
        this(bus, ADDRESS);
    }

    public ADXL345(I2CBus bus, int address) throws IOException {
        this.device = bus.getDevice(address);
    }

    public int readDeviceId() throws IOException {
        return device.read(REG_DEVICE_ID);
    }

    public void checkDevice() throws IOException {
        if (readDeviceId() != DEVICE_ID) {
            throw new IOException("Cannot find ADXL345 at address " + Integer.toHexString(device.getAddress()));
        }
    }

    @Override
    public void start() throws IOException {
        checkDevice();

        // +/- 16g full res
        device.write(REG_DATA_FORMAT, (byte) 0b1011);

        // 400HZ
        device.write(REG_BW_RATE, (byte) 0b1100);

        // put the device in to measurement mode
        device.write(REG_POWER_CTL, (byte) 0x8);
    }

    @Override
    public void stop() throws IOException {
        // take the device out of measurement mode
        device.write(REG_POWER_CTL, (byte) 0x00);
    }

    public VectorMeasure<Acceleration> readVector() throws IOException {
        byte[] data = new byte[6];
        device.read(REG_DATAX0, data, 0, 6);

        ByteBuffer bb = ByteBuffer.allocate(data.length);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.put(data);

        double x = ((double) (bb.getShort(0) + CAL_X)) / SENSITIVITY_X;
        double y = ((double) (bb.getShort(2) + CAL_Y)) / SENSITIVITY_Y;
        double z = ((double) (bb.getShort(4) + CAL_Z)) / SENSITIVITY_Z;

        return VectorMeasure.valueOf(x, y, z, NonSI.G);
    }

    public Measure<Double, Acceleration> readX() throws IOException {
        return read(REG_DATAX0, CAL_X, SENSITIVITY_X);
    }

    public Measure<Double, Acceleration> readY() throws IOException {
        return read(REG_DATAY0, CAL_Y, SENSITIVITY_Y);
    }

    public Measure<Double, Acceleration> readZ() throws IOException {
        return read(REG_DATAZ0, CAL_Z, SENSITIVITY_Z);
    }

    private Measure<Double, Acceleration> read(int reg, double cal, double sensitivity) throws IOException {
        byte[] data = new byte[2];
        device.read(reg, data, 0, 2);

        ByteBuffer bb = ByteBuffer.allocate(data.length);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.put(data);

        double gravity = (bb.getShort(0) + cal) / sensitivity;

        return Measure.valueOf(gravity, NonSI.G);
    }

}
