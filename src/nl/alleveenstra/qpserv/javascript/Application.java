package nl.alleveenstra.qpserv.javascript;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.util.Vector;

import nl.alleveenstra.qpserv.QPserv;

import org.mozilla.javascript.*;

/**
 * This class represents a JavaScript application.
 *
 * @author alle.veenstra@gmail.com
 */
public class Application extends Thread {
    private static final int EVALUATE_TIMEOUT = 100;
    private Context cx;
    MyFactory contextFactory = new MyFactory();
    private Scriptable scope;
    private File javascript;
    private Vector<String> messages = new Vector<String>();

    private long cpuPerSecond = 0;
    private long lastUptime = 0;
    private long lastThreadCpuTime = 0;

    private boolean running = true;

    public Application(File javascript) {
        this.javascript = javascript;
    }

    /**
     * Run the JavaScript file.
     */
    public void run() {
        cx = contextFactory.enterContext();
        scope = cx.initStandardObjects();

        // make the communication channel available in the scope
        java.lang.Object wrappedPipe = Context.javaToJS(QPserv.channelManager(), scope);
        ScriptableObject.putProperty(scope, "pipe", wrappedPipe);

        // make this application available in this scope
        java.lang.Object wrappedApplication = Context.javaToJS(this, scope);
        ScriptableObject.putProperty(scope, "application", wrappedApplication);

        try {
            cx.evaluateReader(scope, new FileReader(javascript), javascript.getName(), 0, null);
            while (running) {
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
     * Deliver a message to the JavaScript application by calling a function with the message and sender as parameters.
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

    public void updateCpuUsage() {
        ThreadMXBean mxThread = ManagementFactory.getThreadMXBean();
        RuntimeMXBean mxRuntime = ManagementFactory.getRuntimeMXBean();
        long threadCpuTime = mxThread.getThreadCpuTime(getId());
        long uptime = mxRuntime.getUptime();
        cpuPerSecond = (threadCpuTime - lastThreadCpuTime) / (uptime - lastUptime);
        lastUptime = uptime;
        lastThreadCpuTime = threadCpuTime;
    }

    public long getCpuPerSecond() {
        return cpuPerSecond;
    }

    public void gracefullyQuit() {
        running = false;
        contextFactory.gracefullyQuit();
    }
}