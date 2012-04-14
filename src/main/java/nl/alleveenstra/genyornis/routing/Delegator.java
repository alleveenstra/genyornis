package nl.alleveenstra.genyornis.routing;

import java.util.ArrayList;
import java.util.List;

import nl.alleveenstra.genyornis.controllers.Admin;
import nl.alleveenstra.genyornis.controllers.Channel;
import nl.alleveenstra.genyornis.controllers.Static;

/**
 * @author alle.veenstra@gmail.com
 */
public abstract class Delegator {

    public static List<Object> getControllers(String packageName) {
        List<Object> controllers = new ArrayList<Object>();
        try {
            Class[] classes = getClasses(packageName);
            for (Class cmdClass : classes) {
                Object handler = cmdClass.newInstance();
                controllers.add(handler);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return controllers;
    }

    /**
     * Use java reflection to find all request handler classes.
     *
     * @param pckgname
     * @return an array of type Class
     * @throws ClassNotFoundException
     */
    private static Class[] getClasses(String pckgname) throws ClassNotFoundException {
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
