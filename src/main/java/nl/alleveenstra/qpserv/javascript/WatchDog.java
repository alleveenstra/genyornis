package nl.alleveenstra.qpserv.javascript;

import java.util.HashMap;
import java.util.Map;

/**
 * @author alle.veenstra@gmail.com
 */
public class WatchDog extends Thread {
    public static final int INCREASE = 20;
    public static final double LEAKAGE = 0.1;
    public static final int INITIALIZATION = 0;
    public static final int THRESHOLD = 1000;
    public static final int CPU_CYCLE_LIMIT = 10000;

    ApplicationPool applicationPool;
    Map<Application, Integer> penalties = new HashMap<Application, Integer>();

    protected WatchDog(ApplicationPool applicationPool) {
        this.applicationPool = applicationPool;
    }

    @Override
    public void run() {
        while (true) {
            try {
                for (Application application : applicationPool.list()) {
                    application.updateCpuUsage();
                    if (application.getCpuPerSecond() > CPU_CYCLE_LIMIT)
                        increasePenalty(application);
                    if (isPunishable(application)) {
                        application.stop();
                    }
                    leakPenalty(application);
                }
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    private boolean isPunishable(Application application) {
        return getPenalty(application) > THRESHOLD;
    }

    public int getPenalty(Application application) {
        if (!penalties.containsKey(application))
            penalties.put(application, INITIALIZATION);
        return penalties.get(application);
    }

    private void leakPenalty(Application application) {
        int penalty = getPenalty(application);
        penalties.put(application, penalty - (int)(penalty * LEAKAGE));
    }

    private void increasePenalty(Application application) {
        penalties.put(application, getPenalty(application) + INCREASE);
    }
}
