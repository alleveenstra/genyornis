package nl.alleveenstra.genyornis.httpd;

import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.alleveenstra.genyornis.ServerContext;
import nl.alleveenstra.genyornis.filters.Chain;
import nl.alleveenstra.genyornis.filters.Filter;
import nl.alleveenstra.genyornis.routing.HttpDelegator;
import nl.alleveenstra.genyornis.sessions.SessionManager;

public class HttpWorker implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(HttpWorker.class);

    Filter delegator = new HttpDelegator();

	public static final String
            HTTP_OK = "200 OK",
			HTTP_REDIRECT = "301 Moved Permanently",
			HTTP_FORBIDDEN = "403 Forbidden",
            HTTP_NOTFOUND = "404 Not Found",
			HTTP_BADREQUEST = "400 Bad Request",
			HTTP_INTERNALERROR = "500 Internal Server Error",
			HTTP_NOTIMPLEMENTED = "501 Not Implemented";

	public static final String MIME_PLAINTEXT = "text/plain",
			MIME_HTML = "text/html",
			MIME_DEFAULT_BINARY = "application/octet-stream";

	private List<ServerDataEvent> queue = new LinkedList<ServerDataEvent>();
    private ServerContext context;

    public HttpWorker(final ServerContext context) {
        this.context = context;
    }

    public void processData(NioServer server, SocketChannel socket, char[] data, int count) {
		char[] dataCopy = new char[count];
		System.arraycopy(data, 0, dataCopy, 0, count);
		synchronized (queue) {
			queue.add(new ServerDataEvent(server, socket, dataCopy));
			queue.notify();
		}
	}

	public void run() {
		ServerDataEvent dataEvent;

		while (true) {
			// Wait for data to become available
			synchronized (queue) {
				while (queue.isEmpty()) {
					try {
						queue.wait();
					} catch (InterruptedException e) {
					}
				}
				dataEvent = queue.remove(0);
			}
			HttpRequest request = HttpRequest.build(dataEvent);
			HttpResponse response = HttpResponse.build();

            Chain chain = new Chain();
            chain.addFilter(delegator);
            chain.addFilter(SessionManager.getInstance());

			// set the chain in motion
			chain.forward(context, request, response);

			if (response.canSend()) {
				sendResponse(request.getSocket(), response);
            }
		}
	}
	
	public void sendResponse(SocketChannel socket, HttpResponse response) {
        context.server().send(socket, response.render());
	}
}
