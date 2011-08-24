@artifact.package@import java.awt.Dialog
import java.awt.Window
import griffon.core.GriffonApplication
import griffon.swing.SwingUtils
import griffon.swing.DefaultWindowDisplayHandler

class @artifact.name@ extends DefaultWindowDisplayHandler {
    void show(Window window, GriffonApplication app) {
        if(!(window instanceof Dialog)) SwingUtils.centerOnScreen(window)
        window.visible = true
    }

    void hide(Window window, GriffonApplication app) {
        if(window instanceof Dialog || app.windowManager.windows.findAll{it.visible}.size() > 1) {
            window.dispose()
        } else {
           if(app.config.shutdown.proceed) window.dispose()
        }
    }
}