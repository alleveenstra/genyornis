package nl.alleveenstra.qpserv.httpd;
import java.nio.channels.SocketChannel;

class ServerDataEvent {
	public NioServer server;
	public SocketChannel socket;
	public char[] data;
	
	public ServerDataEvent(NioServer server, SocketChannel socket, char[] data) {
		this.server = server;
		this.socket = socket;
		this.data = data;
	}
}