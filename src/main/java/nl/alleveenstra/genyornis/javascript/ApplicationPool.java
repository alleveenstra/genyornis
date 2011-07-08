package nl.alleveenstra.genyornis.javascript;

import java.io.File;
import java.util.*;

/**
 * The application pool manages all JavaScript applications. It instantiates them in separate threads.
 *
 * @author alle.veenstra@gmail.com
 */
public class ApplicationPool {

    private static final String SCRIPTFOLDER = "/javascripts";
    private static final String USER_DIR = "user.dir";
    private static ApplicationPool instance;

    private Map<String, Application> apps;
    private WatchDog watchDog;

    private ApplicationPool() {
        apps = new HashMap<String, Application>();
        deployDirectory(System.getProperty(USER_DIR).concat(SCRIPTFOLDER));
        watchDog = new WatchDog(this);
        watchDog.start();
    }

    public static ApplicationPool getInstance() {
        if (instance == null) {
            instance = new ApplicationPool();
        }
        return instance;
    }

    public WatchDog getWatchDog() {
        return watchDog;
    }

    public void start(String appname) {
        Thread app = apps.get(appname);
        if (app != null) {
            app.start();
        }
    }

    public void stop(String appname) {
        Thread app = apps.get(appname);
        if (app != null) {
            app.interrupt();
        }
    }

    public Collection<Application> list() {
        return apps.values();
    }

    public long getCPUTime(String applicationName) {
        if (apps.containsKey(applicationName)) {
            Application application = apps.get(applicationName);
            application.updateCpuUsage();
            return application.getCpuPerSecond();
        }
        return -1;
    }

    private void deployDirectory(String dirname) {
        File directory = new File(dirname);
        if (directory.exists()) {
            String[] files = directory.list();
            for (String file : files) {
                if (!file.startsWith(".")) {
                    File script = new File(directory.getAbsolutePath().concat("/").concat(file));
                    Application app = new Application(script);
                    apps.put(file.toString(), app);
                    app.start();
                }
            }
        }
    }
}
