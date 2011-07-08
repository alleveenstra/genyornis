package nl.alleveenstra.genyornis.controllers;

import nl.alleveenstra.genyornis.Genyornis;
import nl.alleveenstra.genyornis.channels.ChannelManager;
import nl.alleveenstra.genyornis.httpd.HttpContext;
import nl.alleveenstra.genyornis.httpd.HttpRequest;
import nl.alleveenstra.genyornis.httpd.HttpResponse;
import nl.alleveenstra.genyornis.routing.Action;
import nl.alleveenstra.genyornis.routing.Controller;

/**
 * The channel handler class is responsible for handling messaging between users
 * and applications.
 * 
 * @author alle.veenstra@gmail.com
 */
@Controller(prefix = "/channel/")
public class Channel {

  private static final String MESSAGE         = "msg";
  private static final String NAME            = "name";

  /**
   * Handle a message send request. Send a message to a specific channel.
   *
   * @param context
   * @param request
   * @param response
   */
  @Action(regex = "message")
  public void handle_message(HttpContext context, HttpRequest request, HttpResponse response) {
    if (request.getParameters().containsKey(NAME) && request.getParameters().containsKey(MESSAGE)) {
      Genyornis.channelManager().send((String) request.getParameters().get(NAME), (String) request.getParameters().get(MESSAGE));
    }
  }

  /**
   * Handle a listen request. Start listening on a specific channel.
   *
   * @param context
   * @param request
   * @param response
   */
  @Action(regex = "listen")
  public void handle_listen(HttpContext context, HttpRequest request, HttpResponse response) {
    if (request.getParameters().containsKey(NAME))
      ChannelManager.getInstance().join(request.getParameters().get(NAME), request.getSocket());
    response.setSend(false);
  }

  /**
   * Handle a list request. This shows all available channels.
   *
   * @param context
   * @param request
   * @param response
   */
  @Action(regex = "list")
  public void handle_list(HttpContext context, HttpRequest request, HttpResponse response) {
    String content = "<ul>";
    for (String pipe : ChannelManager.getInstance().list())
      content += "<li>" + pipe + "</li>";
    content += "</ul>";
    response.setContent(content.getBytes());
  }
}