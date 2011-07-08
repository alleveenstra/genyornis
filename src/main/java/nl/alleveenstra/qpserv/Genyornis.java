package nl.alleveenstra.qpserv;

import java.net.InetAddress;

import nl.alleveenstra.qpserv.channels.ChannelManager;
import nl.alleveenstra.qpserv.httpd.HttpWorker;
import nl.alleveenstra.qpserv.httpd.NioServer;
import nl.alleveenstra.qpserv.javascript.ApplicationPool;

/**
 * Genyornis is the main application. Configures the HTTP daemon and
 * launches it.
 * 
 * @author alle.veenstra@gmail.com
 */
public class Genyornis {
  private static final int LISTEN_PORT = 1337;
  private static final InetAddress LISTEN_HOST = null;
  
	private static HttpWorker worker = null;
	private static NioServer server = null;
	private static ApplicationPool applications = null;
	
	public static void main(String[] args) {
		applications();
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
}
