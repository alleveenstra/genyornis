package nl.alleveenstra.genyornis.routing;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.alleveenstra.genyornis.httpd.HttpResponse;

/**
 * @author alle.veenstra@gmail.com
 */
public class VelocityTemplate {
    private static final Logger log = LoggerFactory.getLogger(VelocityTemplate.class);

    private Template template;
    private Context velocityContext;

    public VelocityTemplate(String filename) {
        InputStream url = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
        try {
            RuntimeServices runtimeServices = RuntimeSingleton.getRuntimeServices();
            StringReader reader = new StringReader(IOUtils.toString(url));
            SimpleNode node = runtimeServices.parse(reader, "Template name");
            template = new Template();
            template.setRuntimeServices(runtimeServices);
            template.setData(node);
            template.initDocument();
            velocityContext = new VelocityContext();
        } catch (Exception e) {
            log.error("Error while rendering freemarker template", e);
        }
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
