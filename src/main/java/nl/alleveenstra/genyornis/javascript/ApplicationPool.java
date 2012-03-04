package nl.alleveenstra.genyornis.javascript;

import java.io.File;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.alleveenstra.genyornis.ServerContext;

/**
 * The application pool manages all JavaScript applications. It instantiates them in separate threads.
 *
 * @author alle.veenstra@gmail.com
 */
public class ApplicationPool {
    private static final Logger log = LoggerFactory.getLogger(ApplicationPool.class);

    private static final String SCRIPTFOLDER = "/javascripts";
    private static final String USER_DIR = "user.dir";
    private static ApplicationPool instance;

    private Map<String, Application> apps;
    private WatchDog watchDog;
    private ServerContext context;

    private ApplicationPool(ServerContext context) {
        this.context = context;
        apps = new HashMap<String, Application>();
        watchDog = new WatchDog(this);
        watchDog.start();
    }

    public static ApplicationPool getInstance(ServerContext context) {
        if (instance == null) {
            instance = new ApplicationPool(context);
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

    public void deployDirectory(String dirname) {
        File directory = new File(dirname);
        if (directory.exists()) {
            String[] files = directory.list();
            for (String file : files) {
                if (!file.startsWith(".")) {
                    File script = new File(directory.getAbsolutePath().concat("/").concat(file));
                    Application app = new Application(context, script);
                    apps.put(file.toString(), app);
                    app.start();
                }
            }
        }
    }
}
