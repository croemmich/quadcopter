package quadcopter.io.devices;

import quadcopter.Contstants;
import quadcopter.io.Accelerometer;
import quadcopter.io.i2c.I2CBus;
import quadcopter.io.i2c.I2CDevice;
import quadcopter.model.XYZ;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


/**
 * ADXL345 Driver for generic I2C implementations.
 *
 * Influenced by https://github.com/kachun610/ADXL345-Library
 */
public class ADXL345 implements Accelerometer {

    /**
     * ADXL345 address when ALT is connected to HIGH
     */
    public static final int ADDR_ALT_HIGH = 0x1D;

    /**
     * ADXL345 address when ALT is connected to LOW
     */
    public static final int ADDR_ALT_LOW = 0x53;

    /**
     * Interrupt PIN INT1.
     */
    public static final int INT1_PIN = 0x00;

    /**
     * Interrupt PIN INT2.
     */
    public static final int INT2_PIN = 0x01;

    public static final int DEVID = 0xE5;

    public static enum INTERRUPT {
        DATA_READY_BIT(0x07), SINGLE_TAP_BIT(0x06), DOUBLE_TAP_BIT(0x05), ACTIVITY_BIT(0x04),
        INACTIVITY_BIT(0x03), FREE_FALL_BIT(0x02), WATERMARK_BIT(0x01), OVERRUNY_BIT(0x00);

        private final int bit;
        INTERRUPT(int bit) {
            this.bit = bit;
        }
        public int getPosition() {
            return this.bit;
        }
    }

    public static enum REGISTER {

        /**
         * Device ID. Read Only.
         */
        DEVID(0x0),

        /**
         * Tap threshold.
         */
        THRESH_TAP(0x1D),

        /**
         * X-axis offset.
         */
        OFSX(0x1E),

        /**
         * Y-axis offset.
         */
        OFSY(0x1F),

        /**
         * Z-axis offset.
         */
        OFSZ(0x20),

        /**
         * Tap duration.
         */
        DUR(0x21),

        /**
         * Tap latency.
         */
        LATENT(0x22),

        /**
         * Tap window.
         */
        WINDOW(0x23),

        /**
         * Activity threshold.
         */
        THRESH_ACT(0x24),

        /**
         * Inactivity threshold.
         */
        THRESH_INACT(0x25),

        /**
         * Inactivity time.
         */
        TIME_INACT(0x26),

        /**
         * Axis enable control for activity and inactivity detection.
         */
        ACT_INACT_CTL(0x27),

        /**
         * Free-fall threshold.
         */
        THRESH_FF(0x28),

        /**
         * Free-fall time.
         */
        TIME_FF(0x29),

        /**
         * Axis control for tap/double tap.
         */
        TAP_AXES(0x2A),

        /**
         * Source of tap/double tap. Read-only.
         */
        ACT_TAP_STATUS(0x2B),

        /**
         * Data rate and power mode control.
         */
        BW_RATE(0x2C),

        /**
         * Power-saving features control.
         */
        POWER_CTL(0x2D),

        /**
         * Interrupt enable control.
         */
        INT_ENABLE(0x2E),

        /**
         * Interrupt mapping control.
         */
        INT_MAP(0x2F),

        /**
         * Source of interrupts. Read-only.
         */
        INT_SOURCE(0x30),

        /**
         * Data format control.
         */
        DATA_FORMAT(0x31),

        /**
         * X-Axis Data 0. Read-only.
         */
        DATAX0(0x32),

        /**
         * X-Axis Data 1. Read-only.
         */
        DATAX1(0x33),

        /**
         * Y-Axis Data 0. Read-only.
         */
        DATAY0(0x34),

        /**
         * Y-Axis Data 1. Read-only.
         */
        DATAY1(0x35),

        /**
         * Z-Axis Data 0. Read-only.
         */
        DATAZ0(0x36),

        /**
         * Z-Axis Data 1. Read-only.
         */
        DATAZ1(0x37),

        /**
         * FIFO control.
         */
        FIFO_CTL(0x38),

        /**
         * FIFO status. Read-only.
         */
        FIFO_STATUS(0x39);

        private final int address;
        REGISTER(int address){
            this.address = address;
        }
        public int getAddress() {
            return this.address;
        }
    }

    /**
     * The data rate values used in {@link REGISTER#BW_RATE}.
     */
    public static enum DATA_RATE {
        RATE_3200HZ(0b1111), RATE_1600HZ(0b1110), RATE_800HZ(0b1101), RATE_400HZ(0b1100),
        RATE_200HZ(0b1011), RATE_100HZ(0b1010), RATE_50HZ(0b1001), RATE_25HZ(0b1000),
        RATE_12HZ(0b0111), RATE_6HZ(0b0110);
        private final int rate;
        DATA_RATE(int rate) {
            this.rate = rate;
        }
        public int getValue() {
            return this.rate;
        }
    }

    /**
     * The data format values used in {@link REGISTER#DATA_FORMAT}.
     */
    public static enum DATA_FORMAT {
        RANGE_2G(0b00), RANGE_4G(0b01), RANGE_8G(0b10), RANGE_16G(0b11);
        private final int format;
        DATA_FORMAT(int format) {
            this.format = format;
        }
        public int getValue() {
            return this.format;
        }
    }


    private final I2CDevice device;
    private final int address;

    public ADXL345(I2CBus bus) throws IOException {
        this(bus, ADDR_ALT_LOW);
    }

    public ADXL345(I2CBus bus, int address) throws IOException {
        this.device = bus.getDevice(address);
        this.address = address;
    }

    public int readDeviceId() throws IOException {
        return device.read(REGISTER.DEVID.getAddress());
    }

    @Override
    public void start() throws IOException {
        if (readDeviceId() != DEVID) {
            throw new IOException("Device at address " + Integer.toHexString(address) + " is not an ADXL345");
        }
        device.write(REGISTER.DATA_FORMAT.getAddress(), (byte) 0b0001);
        device.write(REGISTER.BW_RATE.getAddress(), (byte) DATA_RATE.RATE_1600HZ.getValue());
        device.write(REGISTER.POWER_CTL.getAddress(), (byte) 0x08);
        //calibrate();
    }

    @Override
    public void stop() throws IOException {
        device.write(REGISTER.POWER_CTL.getAddress(), (byte) 0x00);
    }

    public XYZ getRawXYZ() throws IOException {
        byte[] data = new byte[6];
        device.read(REGISTER.DATAX0.getAddress(), data, 0, 6);

        ByteBuffer bb = ByteBuffer.allocate(data.length);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.put(data);

        double x = bb.getShort(0);
        double y = bb.getShort(2);
        double z = bb.getShort(4);

        return new XYZ(x, y, z);
    };

    @Override
    public XYZ getXYZ() throws IOException {
        XYZ raw = getRawXYZ();
        return new XYZ(raw.x * 0.00376390 * Contstants.GRAVITY, raw.y * 0.00376009 * Contstants.GRAVITY, raw.z * 0.00349265 * Contstants.GRAVITY);
    };

    @Override
    public double getX() throws IOException {
        return getRawX() * 0.00376390 * Contstants.GRAVITY;
    }

    @Override
    public double getY() throws IOException {
        return getRawY() * 0.00376009 * Contstants.GRAVITY;
    }

    @Override
    public double getZ() throws IOException {
        return getRawZ() * 0.00349265 * Contstants.GRAVITY;
    }

    public short getRawX() throws IOException {
        byte[] data = new byte[2];
        device.read(REGISTER.DATAX0.getAddress(), data, 0, 2);
        return (short) (((data[0] & 0xff) << 8) + (data[1] & 0xff));
    }

    public short getRawY() throws IOException {
        byte[] data = new byte[2];
        device.read(REGISTER.DATAY0.getAddress(), data, 0, 2);
        return (short) (((data[0] & 0xff) << 8) + (data[1] & 0xff));
    }

    public short getRawZ() throws IOException {
        byte[] data = new byte[2];
        device.read(REGISTER.DATAZ0.getAddress(), data, 0, 2);
        return (short) (((data[0] & 0xff) << 8) + (data[1] & 0xff));
    }

    public void setDataFormat(DATA_FORMAT format) throws IOException {
        stop();
        device.write(REGISTER.DATA_FORMAT.getAddress(), (byte) format.getValue());
        start();
    }

    public void setDateRate(DATA_RATE rate) throws IOException {
        stop();
        device.write(REGISTER.BW_RATE.getAddress(), (byte) rate.getValue());
        start();
    }

    public void calibrate() throws IOException {
        int samples = 200;

        double totalX = 0;
        double totalY = 0;
        double totalZ = 0;

        for (int i = 0; i < samples; i++) {
            XYZ xyz = getRawXYZ();
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignore) { }
        }

        for (int i = 0; i < samples; i++) {
            XYZ xyz = getRawXYZ();
            totalX += xyz.x;
            totalY += xyz.y;
            totalZ += xyz.z;
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignore) { }
        }

        System.out.println("X OFFSET: " + (totalX / samples));
        device.write(REGISTER.OFSX.getAddress(), (byte) (totalX / samples));

        System.out.println("Y OFFSET: " + (totalY / samples));
        device.write(REGISTER.OFSY.getAddress(), (byte) (totalY / samples));

        System.out.println("Z OFFSET: " + (totalZ / samples));
        device.write(REGISTER.OFSZ.getAddress(), (byte) (totalZ / samples));
    }

}
