package ${package};

import griffon.core.event.XEventHandler;
import griffon.exceptions.GriffonViewInitializationException;

public class ApplicationEventHandler implements XEventHandler {
    public void onUncaughtGriffonViewInitializationException(GriffonViewInitializationException x) {
        System.exit(1);
    }
}