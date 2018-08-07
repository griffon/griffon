package \${groupId}

import griffon.core.event.EventHandler
import griffon.exceptions.GriffonViewInitializationException

class ApplicationEventHandler implements EventHandler {
    void onUncaughtGriffonViewInitializationException(GriffonViewInitializationException x) {
        System.exit(1)
    }
}