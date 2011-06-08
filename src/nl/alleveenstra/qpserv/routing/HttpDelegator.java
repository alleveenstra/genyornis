package nl.alleveenstra.qpserv.routing;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
     * @param packageName
     * @return an array of type Class
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("rawtypes")
    public Class[] getClasses(String packageName) throws ClassNotFoundException {
        ArrayList<Class> classes = new ArrayList<Class>();
        File directory = null;
        try {
            ClassLoader cld = Thread.currentThread().getContextClassLoader();
            if (cld == null) {
                throw new ClassNotFoundException("Can't get class loader.");
            }
            String path = packageName.replace('.', '/');
            URL resource = cld.getResource(path);
            if (resource == null) {
                throw new ClassNotFoundException("No resource for " + path);
            }
            directory = new File(resource.getFile());
        } catch (NullPointerException x) {
            throw new ClassNotFoundException(packageName + " (" + directory + ") does not appear to be a valid package");
        }
        if (directory.exists()) {
            String[] files = directory.list();
            for (int i = 0; i < files.length; i++) {
                if (files[i].endsWith(".class")) {
                    classes.add(Class.forName(packageName + '.' + files[i].substring(0, files[i].length() - 6)));
                }
            }
        } else {
            throw new ClassNotFoundException(packageName + " does not appear to be a valid package");
        }
        Class[] classesA = new Class[classes.size()];
        classes.toArray(classesA);
        return classesA;
    }
}