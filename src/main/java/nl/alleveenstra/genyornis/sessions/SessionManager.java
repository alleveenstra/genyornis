package nl.alleveenstra.genyornis.sessions;

import nl.alleveenstra.genyornis.ServerContext;
import nl.alleveenstra.genyornis.filters.Chain;
import nl.alleveenstra.genyornis.filters.Filter;
import nl.alleveenstra.genyornis.httpd.HttpRequest;
import nl.alleveenstra.genyornis.httpd.HttpResponse;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author alle.veenstra@gmail.com
 */
public class SessionManager extends Filter {
    private static final Logger log = LoggerFactory.getLogger(SessionManager.class);

    private static SessionManager instance;
    private SecureRandom random = new SecureRandom();
    private Map<String, Session> sessions = new HashMap<String, Session>();

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null)
            instance = new SessionManager();
        return instance;
    }

    @Override
    public void process(Chain chain, ServerContext context, HttpRequest request, HttpResponse response) {
        Map<String, String> headers = request.getHeaders();
        String cookie = null;
        if (headers.containsKey("cookie")) {
            String cookieHeader = headers.get("cookie");
            Pattern pattern = Pattern.compile("sessid=([A-Za-z0-9]+)");
            Matcher matcher = pattern.matcher(cookieHeader);
            matcher.find();
            try {
                cookie = matcher.group(1);
            } catch (Exception e) {
                cookie = null;
            }
        }
        if (cookie == null) {
            cookie = new BigInteger(130, random).toString(32);
            response.getHeaders().put("Set-Cookie", "sessid=" + cookie);
        } else {
            if (!sessions.containsKey(cookie))
                sessions.put(cookie, new Session());
            request.setSession(sessions.get(cookie));
        }
        chain.forward(context, request, response);
    }
}
