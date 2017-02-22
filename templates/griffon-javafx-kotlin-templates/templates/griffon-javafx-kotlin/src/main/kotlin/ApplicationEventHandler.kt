package ${project_package}

import griffon.core.event.EventHandler
import griffon.exceptions.GriffonViewInitializationException
import javafx.application.Platform

class ApplicationEventHandler : EventHandler {
    fun onUncaughtGriffonViewInitializationException(x: GriffonViewInitializationException) {
        Platform.exit()
    }
}