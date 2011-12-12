@artifact.package@import java.awt.*;
import javax.swing.*;
import java.util.Arrays;
import java.util.Map;

import net.miginfocom.swing.MigLayout;
import griffon.util.GriffonApplicationUtils;
import griffon.util.GriffonNameUtils;
import griffon.util.ConfigUtils;
import griffon.swing.SwingGriffonApplication;
import org.codehaus.griffon.runtime.core.AbstractGriffonView;
import griffon.plugins.i18n.MessageSourceHolder;

public class @artifact.name@ extends AbstractGriffonView {
    private @artifact.name.plain@Controller controller;
    private @artifact.name.plain@Model model;

    public void setController(@artifact.name.plain@Controller controller) {
        this.controller = controller;
    }

    public void setModel(@artifact.name.plain@Model model) {
        this.model = model;
    }

    // build the UI
    private void init(Container container) {
        MigLayout layout = new MigLayout();
        layout.setLayoutConstraints("fill");
        container.setLayout(layout);
        container.add(@artifact.name.plain@Content.widget(), "center, grow");
        container.add(@artifact.name.plain@StatusBar.statusBar(), "south, grow");
    }

    @Override
    public void mvcGroupInit(Map<String, Object> args) {
        execSync(new Runnable() {
            public void run() {
                try {
                    Container container = (Container) getApp().createApplicationContainer();
                    if (container instanceof Window) {
                        containerPreInit((Window) container);
                    }

                    if (container instanceof JFrame) {
                        JFrame frame = (JFrame) container;
                        frame.setJMenuBar(@artifact.name.plain@MenuBar.menuBar());
                        init(frame.getContentPane());
                    } else if (container instanceof JApplet) {
                        JApplet applet = (JApplet) container;
                        applet.setJMenuBar(@artifact.name.plain@MenuBar.menuBar());
                        init(applet.getContentPane());
                    }

                    if (container instanceof Window) {
                        containerPostInit((Window) container);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void containerPreInit(Window window) {
        if (window instanceof Frame) {
            ((Frame) window).setTitle(GriffonNameUtils.capitalize(
                    message("application.title", ConfigUtils.getConfigValue(getApp().getConfig(), "application.title").toString())));
        }
        window.setIconImage(getImage("/griffon-icon-48x48.png"));
        if (Float.parseFloat(GriffonApplicationUtils.getJavaVersion().substring(0, 3)) > 1.5) {
            window.setIconImages(Arrays.asList(
                    getImage("/griffon-icon-48x48.png"),
                    getImage("/griffon-icon-32x32.png"),
                    getImage("/griffon-icon-16x16.png")
            ));
        }
        window.setLocationByPlatform(true);
        window.setPreferredSize(new Dimension(320, 240));
    }

    private void containerPostInit(Window window) {
        window.pack();
        ((SwingGriffonApplication) getApp()).getWindowManager().attach(window);
    }

    private Image getImage(String path) {
        return Toolkit.getDefaultToolkit().getImage(@artifact.name.plain@View.class.getResource(path));
    }

    private String message(String key, String defaultValue) {
        return MessageSourceHolder.getMessageSource().getMessage(key, defaultValue);
    }
}
