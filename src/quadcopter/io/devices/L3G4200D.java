package quadcopter.io.devices;

import quadcopter.filter.Filter;
import quadcopter.filter.LowPassFilter;
import quadcopter.io.Gyroscope;
import quadcopter.io.Thermometer;
import quadcopter.io.i2c.I2CBus;
import quadcopter.io.i2c.I2CDevice;
import quadcopter.model.Vector;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class L3G4200D implements Gyroscope, Thermometer {

    public static final int DEFAULT_ADDRESS = 105;

    public static final int REG_WHO_AM_I = 0x0F;
    public static final int REG_WHO_AM_I_VALUE = 0xD3;

    public static final int REG_CTRL_REG1 = 0x20;
    public static final int REG_CTRL_REG1_DRBW_800HZ_110 = 0xF0;
    public static final int REG_CTRL_REG1_PD = 0x08;
    public static final int REG_CTRL_REG1_XYZ_ENABLE = 0x07;

    public static final int REG_CTRL_REG2 = 0x21;

    public static final int REG_CTRL_REG3 = 0x22;

    public static final int REG_CTRL_REG4 = 0x23;
    public static final int REG_CTRL_REG4_FS_2000 = 0x30;

    public static final int REG_CTRL_REG5 = 0x24;
    public static final int REG_CTRL_REG5_FIFO_ENABLE = 0x40;

    public static final int REG_TEMP = 0x26;

    public static final int REG_X_LOW = 0x28;

    public static final int REG_FIFO_CTRL = 0x2E;
    public static final int REG_FIFO_CTRL_STREAM = 0x40;

    public static final int REG_FIFO_SRC = 0x2F;
    public static final int REG_FIFO_SRC_MASK = 0x1F;
    public static final int REG_FIFO_SRC_EMPTY = 0x20;
    public static final int REG_FIFO_SRC_OVERRUN = 0x40;

    public static final int REG_AUTO_INCREMENT = 0x80;

    public static final float DPS_DIGIT_2000 = .07f;

    private I2CDevice device;
    private int address;

    private double calX;
    private double calY;
    private double calZ;

    private Filter filterX = new LowPassFilter(800, 20);
    private Filter filterY = new LowPassFilter(800, 20);
    private Filter filterZ = new LowPassFilter(800, 20);

    private Thread thread;
    private boolean running = false;

    public L3G4200D(I2CBus bus) throws IOException {
        this(bus, DEFAULT_ADDRESS);
    }

    public L3G4200D(I2CBus bus, int address) throws IOException {
        this.device = bus.getDevice(address);
        this.address = address;
    }

    private int readWhoAmI() throws IOException {
        return device.read(REG_WHO_AM_I);
    }

    @Override
    public synchronized void start() {
        if (running) return;

        System.out.println("Starting L3G4200D");
        running = true;
        thread = new Thread(loop);
        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                e.printStackTrace();
                stop();
            }
        });
        thread.start();
    }

    @Override
    public synchronized void stop() {
        if (!running) return;

        System.out.println("Stopping L3G4200D");
        running = false;
        try {
            thread.join(1000);
            device.write(REG_CTRL_REG1, (byte) (REG_CTRL_REG1_DRBW_800HZ_110));
        } catch (Exception e) { /* ignore */ }
        thread = null;
    }

    /**
     * main loop to fetch data
     * runs at 200 hz
     */
    private Runnable loop = new Runnable() {
        @Override
        public void run() {
            try {
                if (readWhoAmI() != REG_WHO_AM_I_VALUE) {
                    throw new IOException("Device at address " + Integer.toHexString(address) + " is not an L3G4200D");
                }
                device.write(REG_CTRL_REG1, (byte) (REG_CTRL_REG1_DRBW_800HZ_110 | REG_CTRL_REG1_PD | REG_CTRL_REG1_XYZ_ENABLE));
                device.write(REG_CTRL_REG4, (byte) REG_CTRL_REG4_FS_2000);
                device.write(REG_CTRL_REG5, (byte) REG_CTRL_REG5_FIFO_ENABLE);
                device.write(REG_FIFO_CTRL, (byte) REG_FIFO_CTRL_STREAM);

                TimeUnit.MILLISECONDS.sleep(4);

                calibrate(100);

                while (running) {
                    List<Vector<Double>> data = readBlock();
                    for (Vector<Double> vector : data) {
                        System.out.println(vector);
                    }
                    TimeUnit.MICROSECONDS.sleep((1000 * 1000) / 200);
                }
            } catch (Exception e) {
                stop();
            }
        }
    };

    private List<Vector<Double>> readRawBlock() throws IOException {
        int status = device.read(REG_FIFO_SRC);
        int samples;
        if ((status & REG_FIFO_SRC_OVERRUN) == REG_FIFO_SRC_OVERRUN) {
            samples = 32;
        } else if ((status & REG_FIFO_SRC_EMPTY) == REG_FIFO_SRC_EMPTY) {
            samples = 0;
        } else {
            samples = status & REG_FIFO_SRC_MASK;
        }

        ArrayList<Vector<Double>> data = new ArrayList<>();
        if (samples > 0) {
            byte[] buffer = new byte[samples * 6];
            ByteBuffer bb = ByteBuffer.allocate(buffer.length);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            device.read(REG_X_LOW | REG_AUTO_INCREMENT, buffer, 0, buffer.length);
            bb.put(buffer);
            for (int i = 0; i < samples; i++) {
                final int start = i * 6;

                final double x = filterX.filter(bb.getShort(start));
                final double y = filterY.filter(bb.getShort(start + 2));
                final double z = filterZ.filter(bb.getShort(start + 4));

                data.add(new Vector<>(x, y, z));
            }
        }
        return data;
    }

    private List<Vector<Double>> readBlock() throws IOException {
        List<Vector<Double>> rawData = readRawBlock();
        List<Vector<Double>> data = new ArrayList<>();

        for (Vector<Double> raw : rawData) {
            double x = (raw.x - calX) * DPS_DIGIT_2000;
            double y = (raw.y - calY) * DPS_DIGIT_2000;
            double z = (raw.z - calZ) * DPS_DIGIT_2000;
            data.add(new Vector<>(x, y, z));
        }
        return data;
    }

    public Vector<Double> readVector() throws IOException {
        Vector<Short> raw = readRawVector();

        double x = (raw.x - calX) * DPS_DIGIT_2000;
        double y = (raw.y - calY) * DPS_DIGIT_2000;
        double z = (raw.z - calZ) * DPS_DIGIT_2000;

        return new Vector<>(x, y, z);
    }

    public Vector<Short> readRawVector() throws IOException {
        byte[] data = new byte[6];
        device.read(REG_X_LOW | 0x80, data, 0, data.length);

        ByteBuffer bb = ByteBuffer.allocate(6);
        bb.order(ByteOrder.LITTLE_ENDIAN);
//        bb.put((byte) device.read(REG_X_LOW));
//        bb.put((byte) device.read(REG_X_HIGH));
//        bb.put((byte) device.read(REG_Y_LOW));
//        bb.put((byte) device.read(REG_Y_HIGH));
//        bb.put((byte) device.read(REG_Z_LOW));
//        bb.put((byte) device.read(REG_Z_HIGH));
        bb.put(data);

        return new Vector<>(bb.getShort(0), bb.getShort(2), bb.getShort(4));
    }

    public void calibrate(int samples) throws IOException {
        double xTotal = 0;
        double yTotal = 0;
        double zTotal = 0;

        double count = 0;
        for (int i = 0; i < samples; i++) {
            List<Vector<Double>> raw = readRawBlock();
            for (Vector<Double> vector : raw) {
                xTotal += vector.x;
                yTotal += vector.y;
                zTotal += vector.z;
                count++;
            }
            try {
                TimeUnit.MICROSECONDS.sleep((1000 * 1000) / 200);
            } catch (InterruptedException e) {
                throw new IOException(e.getMessage(), e);
            }
        }

        calX = xTotal / count;
        calY = yTotal / count;
        calZ = zTotal / count;

        System.out.println("Calibration: " + calX + " " + calY + " " + calZ);
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

    @Override
    public double readTemperature() throws IOException {
        return 0;
    }
}
