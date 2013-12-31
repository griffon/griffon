package sample

import griffon.core.GriffonApplication
import org.codehaus.griffon.runtime.core.event.DefaultEventHandler

class Events extends DefaultEventHandler {
    void onBootstrapStart(GriffonApplication application) {
        println("Application is bootstrapping $application")
    }
}
