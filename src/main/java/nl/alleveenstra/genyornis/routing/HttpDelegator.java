package nl.alleveenstra.genyornis.routing;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.alleveenstra.genyornis.ServerContext;
import nl.alleveenstra.genyornis.filters.Chain;
import nl.alleveenstra.genyornis.filters.Filter;
import nl.alleveenstra.genyornis.httpd.HttpRequest;
import nl.alleveenstra.genyornis.httpd.HttpResponse;

/**
 * This class is responsible for the delegation of http requests to their controllers.
 *
 * @author alle.veenstra@gmail.com
 */
public class HttpDelegator extends Delegator implements Filter {
    private static final Logger log = LoggerFactory.getLogger(HttpDelegator.class);

    private static final String CONTROLLERPACKAGE = "nl.alleveenstra.genyornis.controllers";

    /**
     * Process a request.
     *
     * @param request
     * @param response
     */
    public void process(Chain chain, ServerContext context, HttpRequest request, HttpResponse response) {
        for (Object handler : getControllers(CONTROLLERPACKAGE)) {
            Controller accepts = handler.getClass().getAnnotation(Controller.class);
            if (accepts != null && request.getUri().startsWith(accepts.prefix())) {
                String restPath = request.getUri().replaceFirst(accepts.prefix(), "");
                for (Method method : handler.getClass().getMethods()) {
                    Action action = method.getAnnotation(Action.class);
                    final boolean isWebsocket = method.getAnnotation(Websocket.class) != null;
                    if (!isWebsocket && action != null && restPath.matches(action.regex())) {
                        try {
                            method.invoke(handler, new Object[]{context, request, response});
                        } catch (Exception e) {
                            log.error("Unable to invoke controller action", e);
                        }
                    }
                }
            }
        }
        chain.forward(context, request, response);
    }

}