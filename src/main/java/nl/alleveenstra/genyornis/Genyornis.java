package nl.alleveenstra.genyornis;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;

import nl.alleveenstra.genyornis.channels.ChannelManager;
import nl.alleveenstra.genyornis.httpd.HttpWorker;
import nl.alleveenstra.genyornis.httpd.NioServer;
import nl.alleveenstra.genyornis.javascript.ApplicationPool;

/**
 * Genyornis is the main application. Configures the HTTP daemon and
 * launches it.
 *
 * @author alle.veenstra@gmail.com
 */
public class Genyornis {
    private static InetAddress LISTEN_HOST = null;
    private static int LISTEN_PORT = 8080;

    private static HttpWorker worker = null;
    private static NioServer server = null;
    private static ApplicationPool applications = null;


    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please provide the applications folder in the first argument.");
            System.exit(-1);
        }
        LISTEN_PORT = Integer.parseInt(getSettings().getProperty("listen-port"));
        applications().deployDirectory(args[0]);
        new Thread(Genyornis.worker()).start();
        new Thread(Genyornis.server()).start();
    }

    /**
     * Get an instance of the application's application pool.
     *
     * @return an application pool
     */
    public static ApplicationPool applications() {
        if (applications == null)
            applications = ApplicationPool.getInstance();
        return applications;
    }

    /**
     * Get an instance of the application's non-blocking IO server.
     *
     * @return a non-blocking IO server
     */
    public static NioServer server() {
        if (server == null)
            server = new NioServer(LISTEN_HOST, LISTEN_PORT, worker());
        return server;
    }

    /**
     * Get the application's HTTP worker.
     *
     * @return a HTTP worker
     */
    public static HttpWorker worker() {
        if (worker == null)
            worker = new HttpWorker();
        return worker;
    }

    /**
     * Get the channel manager
     *
     * @return the channel manager
     */
    public static ChannelManager channelManager() {
        return ChannelManager.getInstance();
    }

    private static Properties getSettings() {
        //-------------------------------------------------
        Properties myProps = new Properties();
        try {
            InputStream settingsFile = Thread.currentThread().getContextClassLoader().getResourceAsStream("settings.properties");
            myProps.load(settingsFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {

        }

        return myProps;
    }
}
