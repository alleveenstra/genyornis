package nl.alleveenstra.qpserv.channels;

import java.util.HashMap;
import java.util.Map;

import nl.alleveenstra.qpserv.javascript.Application;

/**
 * This class is responsible for hooking applications to channels. Applications
 * are reached using a JavaScript function (callback).
 * 
 * @author alle.veenstra@gmail.com
 */
public class ApplicationHook extends ChannelHook {

  private static Map<String, ApplicationHook> instances = new HashMap<String, ApplicationHook>();
  Application                                 app;
  String                                      callback;

  private ApplicationHook(Application app, String callback) {
    this.app = app;
    this.callback = callback;
  }

  /**
   * Produce an application hook, binding an application to a channel.
   * 
   * @param application
   * @param callback
   * @return an application hook
   */
  public static ApplicationHook produce(Application application, String callback) {
    String key = application.getName().concat("::").concat(callback);
    if (!instances.containsKey(key))
      instances.put(key, new ApplicationHook(application, callback));
    return instances.get(key);
  }

  @Override
  public void deliver(String from, String message) {
    app.deliver(this.callback, from, message);
  }
}