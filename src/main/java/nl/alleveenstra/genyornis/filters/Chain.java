package nl.alleveenstra.genyornis.filters;

import nl.alleveenstra.genyornis.httpd.HttpContext;
import nl.alleveenstra.genyornis.httpd.HttpRequest;
import nl.alleveenstra.genyornis.httpd.HttpResponse;

import java.util.Stack;

/**
 * @author alle.veenstra@gmail.com
 */
public class Chain {
    private Stack<Filter> filters;

    public Chain() {
        filters = new Stack<Filter>();
    }

    public void addFilter(Filter filter) {
        filters.push(filter);
    }

    public void forward(HttpContext context, HttpRequest request, HttpResponse response) {
        if (!filters.empty()) {
            Filter next = filters.pop();
            if (next != null)
                next.process(this, context, request, response);
        }
    }
}
