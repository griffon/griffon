package \${package}

import griffon.core.event.XEventHandler
import griffon.exceptions.GriffonViewInitializationException

class ApplicationEventHandler implements XEventHandler {
    void onUncaughtGriffonViewInitializationException(GriffonViewInitializationException x) {
        System.exit(1)
    }
}