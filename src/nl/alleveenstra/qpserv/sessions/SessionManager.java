package nl.alleveenstra.qpserv.sessions;

import nl.alleveenstra.qpserv.filters.Chain;
import nl.alleveenstra.qpserv.filters.Filter;
import nl.alleveenstra.qpserv.httpd.HttpContext;
import nl.alleveenstra.qpserv.httpd.HttpRequest;
import nl.alleveenstra.qpserv.httpd.HttpResponse;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author alle.veenstra@gmail.com
 */
public class SessionManager extends Filter {

    private static SessionManager instance;
    private SecureRandom random = new SecureRandom();

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
        String cookie = "";
        if (headers.containsKey("Cookie")) {
            String cookieHeader = headers.get("Cookie");
            Pattern pattern = Pattern.compile("sessid=(^[ ;]+)");
            Matcher matcher = pattern.matcher(cookieHeader);
            cookie = matcher.group(0);
            System.out.println("found cookie " + cookie);
        } else {
            cookie = "sessid=" + new BigInteger(130, random).toString(32);
        }
        response.getHeaders().put("Set-Cookie", cookie);

    }
}
