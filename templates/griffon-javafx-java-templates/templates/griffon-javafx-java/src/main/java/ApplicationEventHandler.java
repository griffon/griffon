package ${project_package};

import griffon.core.event.EventHandler;
import griffon.exceptions.GriffonViewInitializationException;
import javafx.application.Platform;

public class ApplicationEventHandler implements EventHandler {
    public void onUncaughtGriffonViewInitializationException(GriffonViewInitializationException x) {
        Platform.exit();
    }
}