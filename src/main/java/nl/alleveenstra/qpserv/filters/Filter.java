package nl.alleveenstra.qpserv.filters;

import nl.alleveenstra.qpserv.httpd.HttpContext;
import nl.alleveenstra.qpserv.httpd.HttpRequest;
import nl.alleveenstra.qpserv.httpd.HttpResponse;

/**
 * @author alle.veenstra@gmail.com
 */
public abstract class Filter {
    public abstract void process(Chain chain, HttpContext context, HttpRequest request, HttpResponse response);
}
