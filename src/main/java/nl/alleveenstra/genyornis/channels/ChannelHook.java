package nl.alleveenstra.genyornis.channels;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A channel hook makes the connection between a socket or an application and a channel.
 *
 * @author alle.veenstra@gmail.com
 */
public abstract class ChannelHook {
    private static final Logger log = LoggerFactory.getLogger(ChannelHook.class);

    /**
     * Deliver a messages.
     *
     * @param from
     * @param message
     */
    public abstract void deliver(String from, String message);
}
