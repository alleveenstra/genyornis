package nl.alleveenstra.qpserv.routing;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author alle.veenstra@gmail.com
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {
    String prefix();
}
