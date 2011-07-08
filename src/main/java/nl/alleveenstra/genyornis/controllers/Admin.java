package nl.alleveenstra.genyornis.controllers;

import nl.alleveenstra.genyornis.httpd.HttpContext;
import nl.alleveenstra.genyornis.httpd.HttpRequest;
import nl.alleveenstra.genyornis.httpd.HttpResponse;
import nl.alleveenstra.genyornis.routing.Action;
import nl.alleveenstra.genyornis.routing.Controller;
import nl.alleveenstra.genyornis.routing.VelocityTemplate;


/**
 * This class handles the admin interface.
 * 
 * It is not ready yet.
 * 
 * @author alle.veenstra@gmail.com
 */
@Controller(prefix = "/admin/")
public class Admin {

    @Action(regex = ".*")
	public void handle(HttpContext context, HttpRequest request, HttpResponse response) {
        VelocityTemplate template = new VelocityTemplate("templates/test.wm");

		template.put("apps", context.getPool().list());
        template.put("watchdog", context.getPool().getWatchDog());
		template.render(response);
	}
}
