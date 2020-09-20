package ${project_package}

import griffon.core.event.EventHandler
import griffon.core.events.UncaughtExceptionThrownEvent
import griffon.exceptions.GriffonViewInitializationException
import javafx.application.Platform

class ApplicationEventHandler implements EventHandler {
    @javax.application.event.EventHandler
    void handleUncaughtExceptionThrownEvent(UncaughtExceptionThrownEvent x) {
        if (x.getThrowable() instanceof GriffonViewInitializationException) {
            Platform.exit()
        }
    }
}