package ${project_package}

import griffon.core.event.EventHandler
import griffon.exceptions.GriffonViewInitializationException
import javafx.application.Platform

class ApplicationEventHandler : EventHandler {
    @javax.application.event.EventHandler
    fun handleUncaughtExceptionThrownEvent(x: UncaughtExceptionThrownEvent) {
        if (x.getThrowable() is GriffonViewInitializationException) {
            Platform.exit()
        }
    }
}