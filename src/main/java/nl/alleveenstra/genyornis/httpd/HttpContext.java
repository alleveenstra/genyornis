package nl.alleveenstra.genyornis.httpd;

import nl.alleveenstra.genyornis.javascript.ApplicationPool;

/**
 * @author alle.veenstra@gmail.com
 */
public class HttpContext {
    ApplicationPool pool;

    public HttpContext(ApplicationPool pool) {
        this.pool = pool;
    }

    public ApplicationPool getPool() {
        return pool;
    }
}
