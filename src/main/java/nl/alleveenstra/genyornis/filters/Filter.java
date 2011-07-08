package nl.alleveenstra.genyornis.filters;

import nl.alleveenstra.genyornis.httpd.HttpContext;
import nl.alleveenstra.genyornis.httpd.HttpRequest;
import nl.alleveenstra.genyornis.httpd.HttpResponse;

/**
 * @author alle.veenstra@gmail.com
 */
public abstract class Filter {
    public abstract void process(Chain chain, HttpContext context, HttpRequest request, HttpResponse response);
}
