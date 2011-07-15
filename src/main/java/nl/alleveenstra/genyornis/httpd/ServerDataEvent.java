package nl.alleveenstra.genyornis.httpd;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ServerDataEvent {
    private static final Logger log = LoggerFactory.getLogger(ServerDataEvent.class);
	public NioServer server;
	public SocketChannel socket;
	public char[] data;
	
	public ServerDataEvent(NioServer server, SocketChannel socket, char[] data) {
		this.server = server;
		this.socket = socket;
		this.data = data;
	}
}