package nl.alleveenstra.qpserv.httpd;

import nl.alleveenstra.qpserv.javascript.ApplicationPool;

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
