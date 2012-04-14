package nl.alleveenstra.genyornis.filters;

import nl.alleveenstra.genyornis.ServerContext;
import nl.alleveenstra.genyornis.httpd.HttpRequest;
import nl.alleveenstra.genyornis.httpd.HttpResponse;

/**
 * @author alle.veenstra@gmail.com
 */
public interface Filter {
    void process(Chain chain, ServerContext context, HttpRequest request, HttpResponse response);
}
