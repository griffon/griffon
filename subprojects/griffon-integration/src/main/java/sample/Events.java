package sample;

import griffon.core.GriffonApplication;
import org.codehaus.griffon.runtime.core.event.DefaultEventHandler;

public class Events extends DefaultEventHandler {
    public void onBootstrapStart(GriffonApplication application) {
        System.out.println("Application is starting " + application);
    }
}
