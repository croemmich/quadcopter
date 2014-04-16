package quadcopter;

import de.taimos.daemon.DaemonLifecycleAdapter;
import de.taimos.daemon.DaemonStarter;
import de.taimos.daemon.LifecyclePhase;

public class QuadcopterDaemon extends DaemonLifecycleAdapter {

    public static void main(String[] args) {
        DaemonStarter.startDaemon("quadcopter", new QuadcopterDaemon());
    }

    @Override
    public void started() {
        Quadcopter.getInstance().start();
    }

    @Override
    public void stopped() {
        Quadcopter.getInstance().stop();
    }

    @Override
    public void aborting() {
        Quadcopter.getInstance().stop();
    }

    @Override
    public void exception(LifecyclePhase phase, Throwable exception) {
        super.exception(phase, exception);
    }
}
