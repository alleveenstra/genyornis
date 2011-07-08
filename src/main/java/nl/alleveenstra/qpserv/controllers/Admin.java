package nl.alleveenstra.qpserv.controllers;

import java.io.StringWriter;

import nl.alleveenstra.qpserv.httpd.HttpContext;
import nl.alleveenstra.qpserv.httpd.HttpRequest;
import nl.alleveenstra.qpserv.httpd.HttpResponse;
import nl.alleveenstra.qpserv.routing.Action;
import nl.alleveenstra.qpserv.routing.Controller;
import nl.alleveenstra.qpserv.routing.VelocityTemplate;

import nl.alleveenstra.qpserv.sessions.Session;


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
