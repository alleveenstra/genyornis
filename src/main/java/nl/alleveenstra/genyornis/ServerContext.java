package nl.alleveenstra.genyornis;

import nl.alleveenstra.genyornis.channels.ChannelManager;
import nl.alleveenstra.genyornis.httpd.HttpWorker;
import nl.alleveenstra.genyornis.httpd.NioServer;
import nl.alleveenstra.genyornis.javascript.ApplicationPool;

/**
 */
public interface ServerContext {
    NioServer server();

    HttpWorker worker();

    ChannelManager channelManager();

    String getApplicationFolder();

    ApplicationPool applications();
}
