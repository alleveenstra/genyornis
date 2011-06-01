package nl.alleveenstra.qpserv.channels;

/**
 * A channel hook makes the connection between a socket or an application
 * and a channel.
 * 
 * @author alle.veenstra@gmail.com
 */
public abstract class ChannelHook {
  
  /**
   * Deliver a messages.
   * 
   * @param from
   * @param message
   */
	public abstract void deliver(String from, String message);
}
