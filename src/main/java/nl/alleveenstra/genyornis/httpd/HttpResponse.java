package nl.alleveenstra.genyornis.httpd;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class HttpResponse {
	private static final String DEFAULT_MESSAGE = "qp<sup>2</sup> HTTPd";
	
  boolean canSend = true;
	byte[] content;
	private Map<String,String> headers = new HashMap<String,String>();
	
	private HttpResponse() {
		content = DEFAULT_MESSAGE.getBytes();
	}
	
	public byte[] getContent() {
		return content;
	}
	
	public Map<String,String> getHeaders() {
		return headers;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public byte[] render() {
		String headers = "HTTP/1.0 200 OK\r\n";
		headers += "Content-Length: " + content.length + "\r\n";
		Iterator<Entry<String,String>> i = this.headers.entrySet().iterator();
		while (i.hasNext()) {
			Entry<String,String> entry = i.next();
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
}
