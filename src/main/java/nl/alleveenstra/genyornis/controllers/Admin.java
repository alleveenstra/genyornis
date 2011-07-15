package nl.alleveenstra.genyornis.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.alleveenstra.genyornis.httpd.HttpContext;
import nl.alleveenstra.genyornis.httpd.HttpRequest;
import nl.alleveenstra.genyornis.httpd.HttpResponse;
import nl.alleveenstra.genyornis.routing.Action;
import nl.alleveenstra.genyornis.routing.Controller;
import nl.alleveenstra.genyornis.routing.VelocityTemplate;
import nl.alleveenstra.genyornis.sessions.Session;


/**
 * This class handles the admin interface.
 * 
 * It is not ready yet.
 * 
 * @author alle.veenstra@gmail.com
 */
@Controller(prefix = "/admin/")
public class Admin {
    private static final Logger log = LoggerFactory.getLogger(Admin.class);

    @Action(regex = ".*")
	public void handle(HttpContext context, HttpRequest request, HttpResponse response) {
        VelocityTemplate template = new VelocityTemplate("templates/test.wm");

        Integer requestCounter = new Integer(0);
        if (request.getSession() != null)
            requestCounter = request.getSession().getValue(Session.TYPES.REQUEST_COUNTER);
        if (requestCounter == null)
            requestCounter = new Integer(0);
        requestCounter++;
        if (request.getSession() != null)
            request.getSession().setValues(Session.TYPES.REQUEST_COUNTER, requestCounter);

        template.put("requestCounter", requestCounter);
		template.put("apps", context.getPool().list());
        template.put("watchdog", context.getPool().getWatchDog());
		template.render(response);
	}
}