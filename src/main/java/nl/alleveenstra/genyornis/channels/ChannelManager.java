package nl.alleveenstra.genyornis.channels;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.alleveenstra.genyornis.javascript.Application;

/**
 * A communication channel manager. Just like in IRC.
 * 
 * @author alle.veenstra@gmail.com
 */
public class ChannelManager {
    private static final Logger log = LoggerFactory.getLogger(ChannelManager.class);

	HashMap<String, Set<ChannelHook>> channels = new HashMap<String, Set<ChannelHook>>();
	
	private static ChannelManager instance = null;
	
	private ChannelManager() {}
	
	/**
	 * Ensure there is only one channel manager.
	 * 
	 * @return a channel manager instance
	 */
	public static ChannelManager getInstance() {
		if (instance == null)
			instance = new ChannelManager();
		return instance;
	}
	
	/**
	 * Remove a hook fram a channel
	 * 
	 * @param socket
	 */
	public void remove(ChannelHook socket) {
		for (Set<ChannelHook> list : channels.values())
			list.remove(socket);
	}
	
	/**
	 * Start listening on a channel as an application.
	 * 
	 * @param name
	 * @param app
	 * @param callback
	 */
	public void join(String name, Application app, String callback) {
		if (!channels.containsKey(name))
			channels.put(name, new HashSet<ChannelHook>());
		channels.get(name).add(ApplicationHook.produce(app, callback));
	}
	
	/**
	 * Start listening on a channel as a socket.
	 * 
	 * @param name
	 * @param socket
	 */
	public void join(String name, SocketChannel socket) {
		if (!channels.containsKey(name))
			channels.put(name, new HashSet<ChannelHook>());
		channels.get(name).add(SocketHook.produce(socket));
	}
	
	/**
	 * List all the channels.
	 * 
	 * @return a set of strings
	 */
	public Set<String> list() {
		return channels.keySet();
	}
	
	/**
	 * List all the listeners of a channel.
	 * 
	 * @param name
	 * 
	 * @return a set of channel hooks
	 */
	public Set<ChannelHook> list(String name) {
		if (!channels.containsKey(name))
			channels.put(name, new HashSet<ChannelHook>());
		return channels.get(name);
	}

	/**
	 * Send a message to a channel.
	 * 
	 * @param name
	 * @param message
	 */
	public void send(String name, String message) {
		if (channels.containsKey(name))
			for (ChannelHook hook : channels.get(name))
				hook.deliver("other", message);
	}
}
