package ${package};

import griffon.core.event.XEventHandler;
import griffon.exceptions.GriffonViewInitializationException;
import javafx.application.Platform;

public class ApplicationEventHandler implements XEventHandler {
    public void onUncaughtGriffonViewInitializationException(GriffonViewInitializationException x) {
        Platform.exit();
    }
}