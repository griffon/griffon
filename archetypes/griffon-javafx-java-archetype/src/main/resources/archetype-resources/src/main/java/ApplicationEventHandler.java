package ${groupId};

import griffon.core.event.EventHandler;
import griffon.core.events.UncaughtExceptionThrownEvent;
import griffon.exceptions.GriffonViewInitializationException;
import javafx.application.Platform;

public class ApplicationEventHandler implements EventHandler {
    @javax.application.event.EventHandler
    public void handleUncaughtExceptionThrownEvent(UncaughtExceptionThrownEvent x) {
        if (x.getThrowable() instanceof GriffonViewInitializationException) {
            Platform.exit();
        }
    }
}