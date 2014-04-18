package quadcopter.io.devices;

import quadcopter.io.Gyroscope;
import quadcopter.io.i2c.I2CBus;
import quadcopter.io.i2c.I2CDevice;
import quadcopter.model.Vector;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class L3G4200D implements Gyroscope {

    public static final int ADDRESS = 105;

    public static final int REG_WHO_AM_I = 0x0F;
    public static final int REG_CTRL_REG1 = 0x20;
    public static final int REG_CTRL_REG2 = 0x21;
    public static final int REG_CTRL_REG3 = 0x22;
    public static final int REG_CTRL_REG4 = 0x23;
    public static final int REG_CTRL_REG5 = 0x24;

    public static final int REG_X_LOW = 0x28;
    public static final int REG_X_HIGH = 0x29;
    public static final int REG_Y_LOW = 0x2A;
    public static final int REG_Y_HIGH = 0x2B;
    public static final int REG_Z_LOW = 0x2C;
    public static final int REG_Z_HIGH = 0x2D;

    public static final int SCALE_2000DPS = 0b10;
    public static final int SCALE_500DPS = 0b01;
    public static final int SCALE_250DPS = 0b00;

    public static final int DATARATE_800HZ_110 = 0b1111;
    public static final int DATARATE_800HZ_50 = 0b1110;
    public static final int DATARATE_800HZ_35 = 0b1101;
    public static final int DATARATE_800HZ_30 = 0b1100;
    public static final int DATARATE_400HZ_110 = 0b1011;
    public static final int DATARATE_400HZ_50 = 0b1010;
    public static final int DATARATE_400HZ_25 = 0b1001;
    public static final int DATARATE_400HZ_20 = 0b1000;
    public static final int DATARATE_200HZ_70 = 0b0111;
    public static final int DATARATE_200HZ_50 = 0b0110;
    public static final int DATARATE_200HZ_25 = 0b0101;
    public static final int DATARATE_200HZ_12_5 = 0b0100;
    public static final int DATARATE_100HZ_25 = 0b0001;
    public static final int DATARATE_100HZ_12_5 = 0b0000;

    public static final float DPS_DIGIT_2000 = .07f;
    public static final float DPS_DIGIT_500 = .0175f;
    public static final float DPS_DIGIT_250 = .00875f;

    public L3G4200D(I2CBus bus) throws IOException {
        this(bus, ADDRESS);
    }

    private I2CDevice device;
    private int address;

    public L3G4200D(I2CBus bus, int address) throws IOException {
        this.device = bus.getDevice(address);
        this.address = address;

    }

    public int readDeviceId() throws IOException {
        return device.read(REG_WHO_AM_I);
    }

    @Override
    public void start() throws IOException {
        if (readDeviceId() != 0xD3) {
            throw new IOException("Device at address " + Integer.toHexString(address) + " is not an L3G4200D");
        }

        device.write(REG_CTRL_REG1, (byte) 0b00001111);
        //device.write(REG_CTRL_REG2, (byte) 0x00);
        //device.write(REG_CTRL_REG3, (byte) 0x08);
        //device.write(REG_CTRL_REG4, (byte) 0b00110000);
        //device.write(REG_CTRL_REG5, (byte) 0x00);
    }

    @Override
    public void stop() throws IOException {

    }

    public Vector<Short> getRawVector() throws IOException {
        byte[] data = new byte[6];
        device.read(REG_X_LOW, data, 0, 6);

        ByteBuffer bb = ByteBuffer.allocate(data.length);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.put(data);

        return new Vector<>(bb.getShort(0), bb.getShort(2), bb.getShort(4));
    }

    public Vector<Float> getVector() throws IOException {
        Vector<Short> raw = getRawVector();

        double f = raw.x * DPS_DIGIT_2000;

        return new Vector<Float>(raw.x * DPS_DIGIT_2000, raw.y * DPS_DIGIT_2000, raw.z * DPS_DIGIT_2000);
    }

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
}
