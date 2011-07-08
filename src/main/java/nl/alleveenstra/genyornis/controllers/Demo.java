package nl.alleveenstra.genyornis.controllers;

import nl.alleveenstra.genyornis.httpd.HttpContext;
import nl.alleveenstra.genyornis.httpd.HttpRequest;
import nl.alleveenstra.genyornis.httpd.HttpResponse;
import nl.alleveenstra.genyornis.routing.Action;
import nl.alleveenstra.genyornis.routing.Controller;
import nl.alleveenstra.genyornis.routing.VelocityTemplate;

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
