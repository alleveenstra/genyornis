package nl.alleveenstra.genyornis;

import java.net.InetAddress;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.alleveenstra.genyornis.channels.ChannelManager;
import nl.alleveenstra.genyornis.fields.ListenAddress;
import nl.alleveenstra.genyornis.fields.Port;
import nl.alleveenstra.genyornis.fields.WebappDirectory;
import nl.alleveenstra.genyornis.httpd.HttpWorker;
import nl.alleveenstra.genyornis.httpd.NioServer;
import nl.alleveenstra.genyornis.javascript.ApplicationPool;

/**
 * Genyornis is the main application. Configures the HTTP daemon and launches it.
 *
 * @author alle.veenstra@gmail.com
 */
public class Genyornis implements Runnable, ServerContext {
    private static final Logger log = LoggerFactory.getLogger(Genyornis.class);

    private InetAddress listenHost = null;
    private int listenPort;

    private HttpWorker worker = null;
    private NioServer server = null;
    private ApplicationPool applications = null;

    private final String APPS_FOLDER = "/apps/";
    private String applicationFolder;

    public static void main(String[] args) {
        Module standalone = new Standalone();
        Injector injector = Guice.createInjector(standalone);
        Genyornis genyornis = injector.getInstance(Genyornis.class);
        genyornis.run();
    }

    @Inject
    public Genyornis(@WebappDirectory String applicationFolder, @ListenAddress InetAddress listen, @Port Integer port) {
        applications().deployDirectory(applicationFolder + APPS_FOLDER);
        this.listenHost = listen;
        this.listenPort = port;
        this.applicationFolder = applicationFolder;
    }

    public void run() {
        new Thread(worker()).start();
        new Thread(server()).start();
        log.info("Application started");
    }

    /**
     * Get an instance of the application's application pool.
     *
     * @return an application pool
     */
    public ApplicationPool applications() {
        if (applications == null) {
            applications = ApplicationPool.getInstance(this);
        }
        return applications;
    }

    /**
     * Get an instance of the application's non-blocking IO server.
     *
     * @return a non-blocking IO server
     */
    public NioServer server() {
        if (server == null) {
            server = new NioServer(this, listenHost, listenPort, worker());
        }
        return server;
    }

    /**
     * Get the application's HTTP worker.
     *
     * @return a HTTP worker
     */
    public HttpWorker worker() {
        if (worker == null) {
            worker = new HttpWorker(this);
        }
        return worker;
    }

    /**
     * Get the channel manager
     *
     * @return the channel manager
     */
    public ChannelManager channelManager() {
        return ChannelManager.getInstance(this);
    }

    public String getApplicationFolder() {
        return applicationFolder;
    }
}
