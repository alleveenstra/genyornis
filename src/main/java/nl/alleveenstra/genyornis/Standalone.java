package nl.alleveenstra.genyornis;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.google.inject.AbstractModule;

import nl.alleveenstra.genyornis.fields.ListenAddress;
import nl.alleveenstra.genyornis.fields.Port;
import nl.alleveenstra.genyornis.fields.WebappDirectory;

/**
 * @author alle.veenstra@gmail.com.
 */
public class Standalone extends AbstractModule {

    @Override
    protected void configure() {
        try {
            bind(InetAddress.class).annotatedWith(ListenAddress.class).toInstance(InetAddress.getByName("0.0.0.0"));
            bind(Integer.class).annotatedWith(Port.class).toInstance(8080);
            bind(String.class).annotatedWith(WebappDirectory.class).toInstance(System.getProperty("user.dir") + "/webdata");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
