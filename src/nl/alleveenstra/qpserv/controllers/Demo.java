package nl.alleveenstra.qpserv.controllers;

import nl.alleveenstra.qpserv.httpd.HttpContext;
import nl.alleveenstra.qpserv.httpd.HttpRequest;
import nl.alleveenstra.qpserv.httpd.HttpResponse;
import nl.alleveenstra.qpserv.routing.Action;
import nl.alleveenstra.qpserv.routing.Controller;
import nl.alleveenstra.qpserv.routing.VelocityTemplate;

/**
 * @author alle.veenstra@gmail.com
 */
@Controller(prefix = "/demo/")
public class Demo {
    @Action(regex = ".*")
	public void handle(HttpContext context, HttpRequest request, HttpResponse response) {
        VelocityTemplate template = new VelocityTemplate("templates/demo/demo.wm");
		template.render(response);
	}
}
