package quadcopter;

public class Quadcopter {

    private static Quadcopter sQuadcopter;
    private LoopingThread mThread;

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

    public synchronized void start() {
        startMainLoop();

//        try {
//            I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);
//            L3G4200D lg = new L3G4200D(bus);
//            lg.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
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
        startMainLoop();
    }

    public void loop() {
        System.out.println("Hello");
    }

    public void onThrowable(Throwable throwable) {
        throwable.printStackTrace();
    }


    public LoopingThread getThread() {
        final LoopingThread thread = new LoopingThread(new Runnable() {
            @Override
            public void run() {
                Quadcopter.getInstance().loop();
            }
        }) {
            @Override
            public void onThrowable(Throwable throwable) {
                Quadcopter.getInstance().onThrowable(throwable);
            }
        };
        thread.setPriority(Thread.MAX_PRIORITY);
        return thread;
    }

    public synchronized void startMainLoop() {
        stopMainLoop();
        mThread = getThread();
        mThread.start();
    }

    public synchronized void stopMainLoop() {
        if (mThread != null) {
            mThread.cancel();
            try {
                mThread.join();
            } catch (InterruptedException e) {
                /* ignore */
            }
            mThread = null;
        }
    }
}