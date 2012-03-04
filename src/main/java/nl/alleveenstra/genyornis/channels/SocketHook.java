package nl.alleveenstra.genyornis.channels;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.alleveenstra.genyornis.ServerContext;
import nl.alleveenstra.genyornis.httpd.HttpResponse;

/**
 * This class is responsible for binding sockets to channels. These sockets are the from the web users.
 *
 * @author alle.veenstra@gmail.com
 */
public class SocketHook extends ChannelHook {
    private static final Logger log = LoggerFactory.getLogger(SocketHook.class);

    private static Map<SocketChannel, SocketHook> instances = new HashMap<SocketChannel, SocketHook>();
    private SocketChannel socket;
    private ServerContext context;

    private SocketHook(ServerContext context, SocketChannel socket) {
        this.socket = socket;
        this.context = context;
    }

    /**
     * @param socket
     * @return
     */
    public static SocketHook produce(ServerContext context, SocketChannel socket) {
        if (!instances.containsKey(socket)) {
            instances.put(socket, new SocketHook(context, socket));
        }
        return instances.get(socket);
    }

    @Override
    public void deliver(String from, String message) {
        HttpResponse response = HttpResponse.build();
        response.setContent(message.getBytes());
        context.worker().sendResponse(socket, response);
    }

}
