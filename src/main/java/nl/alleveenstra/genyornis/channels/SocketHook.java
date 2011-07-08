package nl.alleveenstra.genyornis.channels;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

import nl.alleveenstra.genyornis.Genyornis;
import nl.alleveenstra.genyornis.httpd.HttpResponse;

/**
 * This class is responsible for binding sockets to channels. These sockets are
 * the from the web users.
 * 
 * @author alle.veenstra@gmail.com
 */
public class SocketHook extends ChannelHook {

  private static Map<SocketChannel, SocketHook> instances = new HashMap<SocketChannel, SocketHook>();
  SocketChannel                                 socket;

  private SocketHook(SocketChannel socket) {
    this.socket = socket;
  }

  /**
   * 
   * @param socket
   * @return
   */
  public static SocketHook produce(SocketChannel socket) {
    if (!instances.containsKey(socket))
      instances.put(socket, new SocketHook(socket));
    return instances.get(socket);
  }

  @Override
  public void deliver(String from, String message) {
    HttpResponse response = HttpResponse.build();
    response.setContent(message.getBytes());
    Genyornis.worker().sendResponse(socket, response);
  }

}
