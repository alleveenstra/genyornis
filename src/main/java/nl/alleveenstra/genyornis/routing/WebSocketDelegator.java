package nl.alleveenstra.genyornis.routing;

import java.lang.reflect.Method;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.alleveenstra.genyornis.ServerContext;

public class WebSocketDelegator extends Delegator {
    private static final Logger log = LoggerFactory.getLogger(WebSocketDelegator.class);

    private static final String CONTROLLERPACKAGE = "nl.alleveenstra.genyornis.controllers";

    /**
     * Process websocket
     *
     * @param context
     * @param URI
     * @param data
     */
    public void process(ServerContext context, String URI, SocketChannel socket, String data) {
        for (Object handler : getControllers(CONTROLLERPACKAGE)) {
            Controller accepts = handler.getClass().getAnnotation(Controller.class);
            if (accepts != null && URI.startsWith(accepts.prefix())) {
                String restPath = URI.replaceFirst(accepts.prefix(), "");
                for (Method method : handler.getClass().getMethods()) {
                    Action action = method.getAnnotation(Action.class);
                    Websocket webSocket = method.getAnnotation(Websocket.class);
                    if (webSocket != null && action != null && restPath.matches(action.regex())) {
                        try {
                            method.invoke(handler, new Object[]{context, socket, data});
                        } catch (Exception e) {
                            log.error("Unable to invoke controller action", e);
                        }
                    }
                }
            }
        }
    }
}
