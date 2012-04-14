package nl.alleveenstra.genyornis.httpd;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
    private static final String DEFAULT_MESSAGE = "<h1>Welcome to Genyornis</h1>";

    boolean canSend = true;
    byte[] content;
    private Map<String, String> headers = new HashMap<String, String>();
    private int status = 200;

    private HttpResponse() {
        content = DEFAULT_MESSAGE.getBytes();
    }

    public byte[] getContent() {
        return content;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setContent(byte[] content) {
        if (content == null) {
            this.content = new byte[0];
        } else {
            this.content = content;
        }
    }

    public byte[] render() {
        String headers;
        if (status == 101) {
            headers = "HTTP/1.1 101 Switching Protocols\r\n";
        } else {
            headers = "HTTP/1.0 200 OK\r\n";
            headers += "Content-Length: " + content.length + "\r\n";
        }
        Iterator<Entry<String, String>> i = this.headers.entrySet().iterator();
        while (i.hasNext()) {
            Entry<String, String> entry = i.next();
            headers += entry.getKey() + ": " + entry.getValue() + "\r\n";
        }
        headers += "\r\n";
        byte[] data_headers = headers.getBytes();
        byte[] data = new byte[headers.length() + content.length];
        System.arraycopy(data_headers, 0, data, 0, data_headers.length);
        System.arraycopy(content, 0, data, data_headers.length, content.length);
        return data;
    }

    public static HttpResponse build() {
        HttpResponse response = new HttpResponse();
        response.headers.put("Content-Type", "text/html");
        response.headers.put("Connection", "keep-alive");
        return response;
    }

    public boolean canSend() {
        return canSend;
    }

    public void setSend(boolean value) {
        canSend = value;
    }

    public void setStatus(final int status) {
        this.status = status;
    }
}
