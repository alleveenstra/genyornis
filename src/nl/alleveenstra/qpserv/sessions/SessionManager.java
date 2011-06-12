package nl.alleveenstra.qpserv.sessions;

import nl.alleveenstra.qpserv.filters.Chain;
import nl.alleveenstra.qpserv.filters.Filter;
import nl.alleveenstra.qpserv.httpd.HttpContext;
import nl.alleveenstra.qpserv.httpd.HttpRequest;
import nl.alleveenstra.qpserv.httpd.HttpResponse;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author alle.veenstra@gmail.com
 */
public class SessionManager extends Filter {

    private static SessionManager instance;
    private SecureRandom random = new SecureRandom();
    private Map<String, Session> sessions = new HashMap<String, Session>();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        if (instance == null)
            instance = new SessionManager();
        return instance;
    }


    @Override
    public void process(Chain chain, HttpContext context, HttpRequest request, HttpResponse response) {
        Map<String, String> headers = request.getHeaders();
        String cookie = null;
        if (headers.containsKey("cookie")) {
            String cookieHeader = headers.get("cookie");
            Pattern pattern = Pattern.compile("sessid=([A-Za-z0-9]+)");
            Matcher matcher = pattern.matcher(cookieHeader);
            matcher.find();
            if (matcher.groupCount() >= 1) {
                cookie = matcher.group(1);
            }
        } else {
            cookie = new BigInteger(130, random).toString(32);
            response.getHeaders().put("Set-Cookie", "sessid=" + cookie);
        }
        if (cookie != null) {
            if (!sessions.containsKey(cookie))
                sessions.put(cookie, new Session());
            request.setSession(sessions.get(cookie));
        }
        chain.forward(context, request, response);
    }
}
