package nl.alleveenstra.qpserv.filters;

import nl.alleveenstra.qpserv.httpd.HttpContext;
import nl.alleveenstra.qpserv.httpd.HttpRequest;
import nl.alleveenstra.qpserv.httpd.HttpResponse;

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
