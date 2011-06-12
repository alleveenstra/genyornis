package nl.alleveenstra.qpserv.routing;

import java.io.StringWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;

import nl.alleveenstra.qpserv.httpd.HttpResponse;

/**
 * @author alle.veenstra@gmail.com
 */
public class VelocityTemplate {

    private Template template;
    private Context velocityContext;

    public VelocityTemplate(String filename) {
        template = Velocity.getTemplate(filename);
        velocityContext = new VelocityContext();
    }

    public void put(String name, Object value) {
        velocityContext.put(name, value);
    }

    public void render(HttpResponse response) {
        StringWriter sw = new StringWriter();
        template.merge(velocityContext, sw);
        response.setContent(sw.toString().getBytes());
    }
}
