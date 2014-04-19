package quadcopter;

import quadcopter.io.i2c.I2CBus;
import quadcopter.io.i2c.I2CFactory;

public class Quadcopter {

    private static Quadcopter sQuadcopter;

    public static void main(String[] args) {
        Quadcopter.getInstance().start();
    }

    public static Quadcopter getInstance() {
        if (sQuadcopter == null) {
            sQuadcopter = new Quadcopter();
        }
        return sQuadcopter;
    }

    private Quadcopter() {
    }

    public void start() {

        try {
            I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);

            while (true) {

                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void stop() {


    }

}