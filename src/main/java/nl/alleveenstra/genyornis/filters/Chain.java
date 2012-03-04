package nl.alleveenstra.genyornis.filters;

import java.util.Stack;

import nl.alleveenstra.genyornis.ServerContext;
import nl.alleveenstra.genyornis.httpd.HttpRequest;
import nl.alleveenstra.genyornis.httpd.HttpResponse;

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

    public void forward(ServerContext context, HttpRequest request, HttpResponse response) {
        if (!filters.empty()) {
            Filter next = filters.pop();
            if (next != null) {
                next.process(this, context, request, response);
            }
        }
    }
}
