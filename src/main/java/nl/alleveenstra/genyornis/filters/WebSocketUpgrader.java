package nl.alleveenstra.genyornis.filters;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.alleveenstra.genyornis.ServerContext;
import nl.alleveenstra.genyornis.httpd.HttpRequest;
import nl.alleveenstra.genyornis.httpd.HttpResponse;
import nl.alleveenstra.genyornis.httpd.SocketState;

public class WebSocketUpgrader implements Filter {

    private static final Logger log = LoggerFactory.getLogger(WebSocketUpgrader.class);
    public static final String SECRET = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
    public static final String SEC_WEB_SOCKET_KEY = "sec-websocket-key";
    public static final String UPGRADE = "upgrade";
    public static final String WEBSOCKET = "websocket";

    public void process(final Chain chain, final ServerContext context, final HttpRequest request, final HttpResponse response) {
        if (request.getHeaders().containsKey(UPGRADE) && WEBSOCKET.equals(request.getHeaders().get(UPGRADE))) {
            final String key = request.getHeaders().get(SEC_WEB_SOCKET_KEY);
            if (key != null) {
                String rawResponse = key + SECRET;
                final String handshake = toBase64(toSHA1(rawResponse.getBytes()));
                log.info("Sending handshake " + handshake);
                response.getHeaders().clear();
                response.getHeaders().put("Connection", "Upgrade");
                response.getHeaders().put("Upgrade", "WebSocket");
                response.getHeaders().put("Sec-WebSocket-Accept", handshake);
                response.getHeaders().put("Sec-WebSocket-Version", "13");
                response.getHeaders().put("Access-Control-Allow-Origin", "localhost");
                response.getHeaders().put("Host", "127.0.0.1");
                response.setContent(null);

                response.setStatus(101);
                response.setSend(true);
                context.server().setSocketState(request.getSocket(), SocketState.WEBSOCKET);
                context.server().setWebsocketURI(request.getSocket(), request.getUri());
                log.info("Websocket established");
            } else {
                log.warn("Unable to find WebSocket upgrade key");
            }
        } else {
            chain.forward(context, request, response);
        }
    }

    private static byte[] toSHA1(byte[] data) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-1 algorithm not found");
        }
        return md.digest(data);
    }

    private static String toBase64(byte[] data) {
        return Base64.encodeBase64String(data);
    }
}
