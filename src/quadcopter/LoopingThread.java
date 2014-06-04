package quadcopter;

public abstract class LoopingThread extends Thread implements Thread.UncaughtExceptionHandler {

    private boolean run = true;
    private int delay = 5;
    private long delta = 0;
    private long deltaNoDelay = 0;

    public LoopingThread(Runnable runnable) {
        super(runnable);
        setUncaughtExceptionHandler(this);
    }

    @Override
    public final void run() {
        while (run) {
            long start = System.nanoTime();
            try {
                super.run();
            } catch (Exception e) {
                onThrowable(e);
            }
            deltaNoDelay = System.nanoTime() - start;
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            delta = System.nanoTime() - start;
        }
    }

    /**
     * Called when an uncaught throwable is thrown
     */
    public abstract void onThrowable(Throwable throwable);

    /**
     * Set the time in ms to delay between calling run
     *
     * @param ms
     */
    public void setDelay(int ms) {
        delay = ms;
    }

    /**
     * Stops the thread
     */
    public void cancel() {
        run = false;
    }

    /**
     * @return the time in nanoseconds the last loop took. Includes the delay time.
     */
    public long getDelta() {
        return delta;
    }

    /**
     * @return the time in nanoseconds the last loop took.
     */
    public long getDeltaNoDelay() {
        return deltaNoDelay;
    }

    @Override
    public final void uncaughtException(Thread t, Throwable e) {
        onThrowable(e);
    }

}