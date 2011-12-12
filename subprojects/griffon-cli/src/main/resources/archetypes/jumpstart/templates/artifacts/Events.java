import java.util.List;
import java.awt.Window;
import javax.swing.JOptionPane;
import griffon.core.GriffonApplication;
import griffon.core.ShutdownHandler;
import griffon.swing.SwingGriffonApplication;
import griffon.plugins.i18n.MessageSourceHolder;

public class Events {
    public void onBootstrapEnd(GriffonApplication app) {
        app.getConfig().put("shutdown.proceed", false);
        app.addShutdownHandler(new ShutdownHandler() {
                public boolean canShutdown(GriffonApplication app) {
                    List<Window> windows = ((SwingGriffonApplication) app).getWindowManager().getWindows();
                    Window window = null;
                    for(Window w : windows) {
                        if(w.isFocused()) {
                            window = w;
                            break;
                        }
                    }
                    boolean proceed = JOptionPane.showConfirmDialog(
                            window,
                            message("application.confirm.shutdown.message", "Do you really want to exit?"),
                            message("application.confirm.shutdown.title","Exit"),
                            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;                   
                    app.getConfig().put("shutdown.proceed", proceed);
                    return proceed;
                }
                public void onShutdown(GriffonApplication app) { }
        });
    }

    public void onShutdownAborted(GriffonApplication app) {
        app.getConfig().put("shutdown.proceed", false);
    }

    private String message(String key, String defaultValue) {
        return MessageSourceHolder.getMessageSource().getMessage(key, defaultValue);
    }
}