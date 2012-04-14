package nl.alleveenstra.genyornis.httpd;

import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.alleveenstra.genyornis.ServerContext;
import nl.alleveenstra.genyornis.filters.Chain;
import nl.alleveenstra.genyornis.filters.Filter;
import nl.alleveenstra.genyornis.filters.WebSocketUpgrader;
import nl.alleveenstra.genyornis.routing.HttpDelegator;
import nl.alleveenstra.genyornis.routing.WebSocketDelegator;
import nl.alleveenstra.genyornis.sessions.SessionManager;

public class HttpWorker implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(HttpWorker.class);

    private static List<Filter> filters = new ArrayList<Filter>();

    static {
        filters.add(new WebSocketUpgrader());
        filters.add(new HttpDelegator());
        filters.add(SessionManager.getInstance());
    }

    private List<ServerDataEvent> queue = new LinkedList<ServerDataEvent>();
    private ServerContext context;
    private WebSocketDelegator webSocketDelegator = new WebSocketDelegator();

    public HttpWorker(final ServerContext context) {
        this.context = context;
    }

    public void processData(NioServer server, SocketChannel socket, byte[] data, int count) {
        byte[] dataCopy = new byte[count];
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
            handle(dataEvent);
        }
    }

    private void handle(final ServerDataEvent dataEvent) {
        SocketState socketState = context.server().getSocketState(dataEvent.getSocket());
        switch (socketState) {
            case HTTP:
                handleHttp(dataEvent);
                break;
            case WEBSOCKET:
                handleWebsocket(dataEvent);
                break;
            default:
                log.error("Unknown socket state ", socketState);
        }
    }

    private void handleWebsocket(final ServerDataEvent dataEvent) {
        byte[] frame;
        try {
            frame = FrameReader.translateSingleFrame(dataEvent.getData());
            String strData = new String(frame, Charset.forName("UTF-8"));
            String URI = context.server().getWebsocketURI(dataEvent.getSocket());
            webSocketDelegator.process(context, URI, dataEvent.getSocket(), strData);
        } catch (Exception e) {
            log.error("Error translating frame", e);
        }
    }

    private void handleHttp(final ServerDataEvent dataEvent) {
        HttpRequest request = HttpRequest.build(dataEvent);
        HttpResponse response = HttpResponse.build();

        Chain chain = new Chain();
        for (Filter filter : filters) {
            chain.addFilter(filter);
        }

        chain.forward(context, request, response);

        if (response.canSend()) {
            sendResponse(request.getSocket(), response);
        }
    }

    public void sendResponse(SocketChannel socket, HttpResponse response) {
        context.server().send(socket, response.render());
    }
}
