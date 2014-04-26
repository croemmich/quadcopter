package quadcopter;

import quadcopter.io.devices.L3G4200D;
import quadcopter.io.i2c.I2CBus;
import quadcopter.io.i2c.I2CFactory;
import quadcopter.model.Vector;

import java.io.IOException;

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
            L3G4200D lg = new L3G4200D(bus);
            lg.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        try {
//            I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);
//            L3G4200D lg = new L3G4200D(bus);
//            lg.start();
//            lg.calibrate(333);
//
//            long lastNanotime = System.nanoTime();
//
//            double x = 0;
//            double y = 0;
//            double z = 0;
//            double nextPrint = System.currentTimeMillis() ;
//
//            double start = System.currentTimeMillis();
//
//            while (start + (60 * 1000) > System.currentTimeMillis()) {
//                Vector<Double> vector = lg.readVector();
//                System.out.println(vector);
////
////                long currentNano = System.nanoTime();
////                x += vector.x * (currentNano - lastNanotime) / 1000000000;
////                y += vector.y * (currentNano - lastNanotime) / 1000000000;
////                z += vector.z * (currentNano - lastNanotime) / 1000000000;
//                if (System.currentTimeMillis() > nextPrint) {
//                    //System.out.println(x + " " + y + " " + z);
//                    //System.out.println(vector);
//                    nextPrint += 1000;
//                }
////
////                lastNanotime = currentNano;
//                Thread.sleep(100);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

    public void stop() {


    }

}