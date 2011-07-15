package nl.alleveenstra.genyornis.routing;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.alleveenstra.genyornis.controllers.Admin;
import nl.alleveenstra.genyornis.controllers.Channel;
import nl.alleveenstra.genyornis.controllers.Static;
import nl.alleveenstra.genyornis.filters.Chain;
import nl.alleveenstra.genyornis.filters.Filter;
import nl.alleveenstra.genyornis.httpd.HttpContext;
import nl.alleveenstra.genyornis.httpd.HttpRequest;
import nl.alleveenstra.genyornis.httpd.HttpResponse;

/**
 * This class is responsible for the delegation of http requests to their controllers.
 *
 * @author alle.veenstra@gmail.com
 */
public class HttpDelegator extends Filter {
    private static final Logger log = LoggerFactory.getLogger(HttpDelegator.class);

    private static final String CONTROLLERPACKAGE = "nl.alleveenstra.genyornis.controllers";
    List<Object> all_handlers;

    @SuppressWarnings("rawtypes")
    public HttpDelegator() {
        all_handlers = new ArrayList<Object>();
        try {
            Class[] classes = getClasses(CONTROLLERPACKAGE);
            for (Class cmdClass : classes) {
                Object handler = cmdClass.newInstance();
                all_handlers.add(handler);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Process a request.
     *
     * @param request
     * @param response
     */
    public void process(Chain chain, HttpContext context, HttpRequest request, HttpResponse response) {
        for (Object handler : all_handlers) {
            Controller accepts = handler.getClass().getAnnotation(Controller.class);
            if (accepts != null && request.getUri().startsWith(accepts.prefix())) {
                String restPath = request.getUri().replaceFirst(accepts.prefix(), "");
                for (Method method : handler.getClass().getMethods()) {
                    Action action = method.getAnnotation(Action.class);
                    if (action != null && restPath.matches(action.regex())) {
                        try {
                            method.invoke(handler, new Object[]{context, request, response});
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        chain.forward(context, request, response);
    }

    /**
     * Use java reflection to find all request handler classes.
     *
     * @param pckgname
     * @return an array of type Class
     * @throws ClassNotFoundException
     */
    public static Class[] getClasses(String pckgname) throws ClassNotFoundException {
        ArrayList classes = new ArrayList();

        // TODO: use guice reflection annotation scanner
        classes.add(Admin.class);
        classes.add(Channel.class);
        classes.add(Static.class);

        Class[] classesA = new Class[classes.size()];
        classes.toArray(classesA);
        return classesA;
    }
}