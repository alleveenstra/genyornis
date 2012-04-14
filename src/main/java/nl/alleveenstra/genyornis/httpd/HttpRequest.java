package nl.alleveenstra.genyornis.httpd;

import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.alleveenstra.genyornis.sessions.Session;

public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);
    protected SocketChannel socket;
    protected String method = "";
    protected String uri = "";
    protected Session session = null;
    protected Map<String, String> headers = new HashMap<String, String>();
    protected Map<String, String> parameters = new HashMap<String, String>();

    private HttpRequest() {
    }

    public static HttpRequest build(ServerDataEvent dataEvent) {

        HttpRequest request = new HttpRequest();

        request.socket = dataEvent.getSocket();

        StringTokenizer in = new StringTokenizer(new String(dataEvent.getData(), Charset.forName("UTF-8")), "\r\n");

        // Read the request line
        String inLine = in.nextToken();
        if (inLine == null) {
            return request;
        }
        StringTokenizer st = new StringTokenizer(inLine);

        // if ( !st.hasMoreTokens())
        // sendError( HTTP_BADREQUEST,
        // "BAD REQUEST: Syntax error. Usage: GET /example/file.html" );

        request.method = st.nextToken();

        // if ( !st.hasMoreTokens())
        // sendError( HTTP_BADREQUEST,
        // "BAD REQUEST: Missing URI. Usage: GET /example/file.html" );

        request.uri = st.nextToken();

        // Decode parameters from the URI
        int qmi = request.uri.indexOf('?');
        if (qmi >= 0) {
            decodeParms(request.uri.substring(qmi + 1), request.parameters);
            request.uri = decodePercent(request.uri.substring(0, qmi));
        } else {
            request.uri = decodePercent(request.uri);
        }

        // If there's another token, it's protocol version,
        // followed by HTTP headers. Ignore version but parse headers.
        // NOTE: this now forces header names uppercase since they are
        // case insensitive and vary by client.
        if (in.hasMoreTokens()) {
            String line = in.nextToken();
            while (line.trim().length() > 0) {
                int p = line.indexOf(':');
                request.headers.put(line.substring(0, p).trim().toLowerCase(), line.substring(p + 1).trim());
                line = (in.hasMoreTokens()) ? in.nextToken() : "";
            }
        }

        return request;
    }

    private static void decodeParms(String parms, Map<String, String> p) {
        if (parms == null) {
            return;
        }

        StringTokenizer st = new StringTokenizer(parms, "&");
        while (st.hasMoreTokens()) {
            String e = st.nextToken();
            int sep = e.indexOf('=');
            if (sep >= 0) {
                p.put(decodePercent(e.substring(0, sep)).trim(),
                        decodePercent(e.substring(sep + 1)));
            }
        }
    }

    private static String decodePercent(String str) {
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                switch (c) {
                    case '+':
                        sb.append(' ');
                        break;
                    case '%':
                        sb.append((char) Integer.parseInt(str.substring(i + 1, i + 3), 16));
                        i += 2;
                        break;
                    default:
                        sb.append(c);
                        break;
                }
            }
            return new String(sb.toString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public SocketChannel getSocket() {
        return socket;
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return "HttpRequest [method=" + method + ", uri=" + uri + ", headers=" + headers + ", parameters=" + parameters + "]";
    }
}