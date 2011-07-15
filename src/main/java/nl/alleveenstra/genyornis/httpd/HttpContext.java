package nl.alleveenstra.genyornis.httpd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.alleveenstra.genyornis.javascript.ApplicationPool;

/**
 * @author alle.veenstra@gmail.com
 */
public class HttpContext {
    private static final Logger log = LoggerFactory.getLogger(HttpContext.class);
    ApplicationPool pool;

    public HttpContext(ApplicationPool pool) {
        this.pool = pool;
    }

    public ApplicationPool getPool() {
        return pool;
    }
}
