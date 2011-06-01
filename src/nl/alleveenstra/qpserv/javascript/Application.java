package nl.alleveenstra.qpserv.javascript;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import nl.alleveenstra.qpserv.QPserv;

import sun.org.mozilla.javascript.internal.Context;
import sun.org.mozilla.javascript.internal.Scriptable;
import sun.org.mozilla.javascript.internal.ScriptableObject;

/**
 * This class represents a JavaScript application.
 * 
 * @author alle.veenstra@gmail.com
 */
public class Application extends Thread {
  private static final int EVALUATE_TIMEOUT = 100;
  private Context          cx;
  private Scriptable       scope;
  private File             javascript;
  private Vector<String>   messages         = new Vector<String>();

  public Application(File javascript) {
    this.javascript = javascript;
  }

  /**
   * Run the JavaScript file.
   */
  public void run() {
    cx = Context.enter();
    scope = cx.initStandardObjects();

    // make the communication channel available in the scope
    java.lang.Object wrappedPipe = Context.javaToJS(QPserv.channelManager(), scope);
    ScriptableObject.putProperty(scope, "pipe", wrappedPipe);

    // make this application available in this scope
    java.lang.Object wrappedApplication = Context.javaToJS(this, scope);
    ScriptableObject.putProperty(scope, "application", wrappedApplication);

    try {
      cx.evaluateReader(scope, new FileReader(javascript), javascript.getName(), 0, null);
      while (true) {
        try {
          String message = getMessage();
          cx.evaluateString(scope, message, "<cmd>", 1, null);
          sleep(EVALUATE_TIMEOUT);
        } catch (Exception e) {
          // TODO implement some decent logging
          e.printStackTrace();
        }
      }
    } catch (FileNotFoundException e) {
      // TODO implement some decent logging
      e.printStackTrace();
    } catch (IOException e) {
      // TODO implement some decent logging
      e.printStackTrace();
    }
  }

  /**
   * Read one message from the queue.
   * 
   * @return a message
   * @throws InterruptedException
   */
  public synchronized String getMessage() throws InterruptedException {
    notify();
    while (messages.size() == 0)
      wait();
    String message = (String) messages.firstElement();
    messages.removeElement(message);
    return message;
  }

  /**
   * Deliver a message to the JavaScript application.
   * 
   * @param callback
   * @param from
   * @param message
   */
  public synchronized void deliver(String callback, String from, String message) {
    String code = callback + "('" + from.replace("'", "\'") + "','" + message.replace("'", "\'") + "')";
    messages.add(code);
    notify();
  }
}