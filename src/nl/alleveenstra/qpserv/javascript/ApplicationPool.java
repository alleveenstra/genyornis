package nl.alleveenstra.qpserv.javascript;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * The application pool manages all JavaScript applications. It instantiates them in separate threads.
 *
 * @author alle.veenstra@gmail.com
 */
public class ApplicationPool {

    private static final String SCRIPTFOLDER = "/javascripts";
    private static final String USER_DIR = "user.dir";
    private static ApplicationPool instance;

    HashMap<String, Thread> apps;

    private ApplicationPool() {
        apps = new HashMap<String, Thread>();
        deployDirectory(System.getProperty(USER_DIR).concat(SCRIPTFOLDER));
    }

    public static ApplicationPool getInstance() {
        if (instance == null) {
            instance = new ApplicationPool();
        }
        return instance;
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

    public Set<String> list() {
        return apps.keySet();
    }

    private void deployDirectory(String dirname) {
        File directory = new File(dirname);
        if (directory.exists()) {
            String[] files = directory.list();
            for (String file : files) {
                if (!file.startsWith(".")) {
                    File script = new File(directory.getAbsolutePath().concat("/").concat(file));
                    Thread app = new Thread(new Application(script));
                    apps.put(file.toString(), app);
                    app.start();
                }
            }
        }
    }
}
