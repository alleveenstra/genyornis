package nl.alleveenstra.genyornis.httpd;

import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ServerDataEvent {
    private static final Logger log = LoggerFactory.getLogger(ServerDataEvent.class);
    private NioServer server;
    private SocketChannel socket;
    private byte[] data;

    public ServerDataEvent(NioServer server, SocketChannel socket, byte[] data) {
        this.server = server;
        this.socket = socket;
        this.data = data;
    }

    public NioServer getServer() {
        return server;
    }

    public SocketChannel getSocket() {
        return socket;
    }

    public byte[] getData() {
        return data;
    }
}