package nl.alleveenstra.qpserv.routing;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author alle.veenstra@gmail.com
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Action {
    String regex() default ".*";
}
