package nl.alleveenstra.genyornis.sessions;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author alle.veenstra@gmail.com
 */
public class Session {
    private static final Logger log = LoggerFactory.getLogger(Session.class);

    public static enum TYPES {
        REQUEST_COUNTER
    }

    private Map<TYPES, Object> values = new HashMap<TYPES, Object>();

    public <T> T getValue(TYPES type) {
        if (values.containsKey(type))
           return (T) values.get(type);
        return null;
    }

    public void setValues(TYPES type, Object value) {
        this.values.put(type, value);
    }
}
