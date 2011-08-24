@artifact.package@import java.util.List;
import java.awt.Dialog;
import java.awt.Window;
import griffon.core.GriffonApplication;
import griffon.swing.*;
import static griffon.util.ConfigUtils.getConfigValueAsBoolean;

public class @artifact.name@ extends DefaultWindowDisplayHandler {
    public void show(Window window, GriffonApplication application) {
        if(!(window instanceof Dialog)) SwingUtils.centerOnScreen(window);
        window.setVisible(true);
    }

    public void hide(Window window, GriffonApplication application) {
        List<Window> windows = getWindowManager(application).getWindows();
        int visibleWindows = 0;
        for(Window w : windows) {
            if(w.isVisible()) visibleWindows++;
        }
        if(window instanceof Dialog || visibleWindows > 1) {
            window.dispose();
        } else {
            if(getConfigValueAsBoolean(application.getConfig(), "shutdown.proceed")) window.dispose();
        }
    }

    private WindowManager getWindowManager(GriffonApplication application) {
        return ((SwingGriffonApplication) application).getWindowManager();
    }
}