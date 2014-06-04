package quadcopter.io.i2c.impl;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SynchronizedI2CDeviceImpl extends I2CDeviceImpl {

    public SynchronizedI2CDeviceImpl(I2CBusImpl bus, int address) {
        super(bus, address);
    }

    private final static Lock lock = new ReentrantLock(true);

    @Override
    public void write(byte b) throws IOException {
        lock.lock();
        try {
            super.write(b);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void write(byte[] buffer, int offset, int size) throws IOException {
        lock.lock();
        try {
            super.write(buffer, offset, size);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void write(int address, byte b) throws IOException {
        lock.lock();
        try {
            super.write(address, b);
        } catch (IOException e) {
            super.write(address, b);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void write(int address, byte[] buffer, int offset, int size) throws IOException {
        lock.lock();
        try {
            super.write(address, buffer, offset, size);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int read() throws IOException {
        lock.lock();
        try {
            return super.read();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int read(byte[] buffer, int offset, int size) throws IOException {
        lock.lock();
        try {
            return super.read(buffer, offset, size);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int read(int address) throws IOException {
        lock.lock();
        try {
            return super.read(address);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int read(int address, byte[] buffer, int offset, int size) throws IOException {
        lock.lock();
        try {
            return super.read(address, buffer, offset, size);
        } finally {
            lock.unlock();
        }
    }

}
