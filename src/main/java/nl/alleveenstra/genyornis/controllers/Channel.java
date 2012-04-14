package nl.alleveenstra.genyornis.controllers;

import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.alleveenstra.genyornis.ServerContext;
import nl.alleveenstra.genyornis.httpd.HttpRequest;
import nl.alleveenstra.genyornis.httpd.HttpResponse;
import nl.alleveenstra.genyornis.routing.Action;
import nl.alleveenstra.genyornis.routing.Controller;
import nl.alleveenstra.genyornis.routing.Websocket;

/**
 * The channel handler class is responsible for handling messaging between users and applications.
 *
 * @author alle.veenstra@gmail.com
 */
@Controller(prefix = "/channel/")
public class Channel {
    private static final Logger log = LoggerFactory.getLogger(Channel.class);

    private static final String MESSAGE = "msg";
    private static final String NAME = "name";

    @Action(regex = "chat")
    @Websocket
    public void handle_chat(ServerContext context, SocketChannel socket, String data) {
        context.server().sendWebSocket(socket, data);
    }

    /**
     * Handle a message send request. Send a message to a specific channel.
     *
     * @param context
     * @param request
     * @param response
     */
    @Action(regex = "message")
    public void handle_message(ServerContext context, HttpRequest request, HttpResponse response) {
        if (request.getParameters().containsKey(NAME) && request.getParameters().containsKey(MESSAGE)) {
            context.channelManager().send(request.getParameters().get(NAME), request.getParameters().get(MESSAGE));
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
    public void handle_listen(ServerContext context, HttpRequest request, HttpResponse response) {
        if (request.getParameters().containsKey(NAME)) {
            context.channelManager().join(request.getParameters().get(NAME), request.getSocket());
        }
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
    public void handle_list(ServerContext context, HttpRequest request, HttpResponse response) {
        String content = "<ul>";
        for (String pipe : context.channelManager().list()) {
            content += "<li>" + pipe + "</li>";
        }
        content += "</ul>";
        response.setContent(content.getBytes());
    }
}