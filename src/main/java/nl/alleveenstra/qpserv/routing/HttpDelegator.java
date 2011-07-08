package nl.alleveenstra.qpserv.routing;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import nl.alleveenstra.qpserv.controllers.Admin;
import nl.alleveenstra.qpserv.controllers.Channel;
import nl.alleveenstra.qpserv.controllers.Demo;
import nl.alleveenstra.qpserv.controllers.Static;
import nl.alleveenstra.qpserv.filters.Chain;
import nl.alleveenstra.qpserv.filters.Filter;
import nl.alleveenstra.qpserv.httpd.HttpContext;
import nl.alleveenstra.qpserv.httpd.HttpRequest;
import nl.alleveenstra.qpserv.httpd.HttpResponse;

/**
 * This class is responsible for the delegation of http requests to their controllers.
 *
 * @author alle.veenstra@gmail.com
 */
public class HttpDelegator extends Filter {

    private static final String CONTROLLERPACKAGE = "nl.alleveenstra.qpserv.controllers";
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
        classes.add(Demo.class);
        classes.add(Static.class);

        Class[] classesA = new Class[classes.size()];
        classes.toArray(classesA);
        return classesA;
    }
}