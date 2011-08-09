@artifact.package@import java.awt.*;
import javax.swing.*;
import java.util.Map;

import griffon.swing.SwingGriffonApplication;
import org.codehaus.griffon.runtime.core.AbstractGriffonView;

public class @artifact.name@ extends AbstractGriffonView {
    // these will be injected by Griffon
    private @artifact.name.plain@Controller controller;
    private @artifact.name.plain@Model model;

    public void setController(@artifact.name.plain@Controller controller) {
        this.controller = controller;
    }

    public void setModel(@artifact.name.plain@Model model) {
        this.model = model;
    }

    // build the UI
    private JComponent init() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Content Goes Here"), BorderLayout.CENTER);
        return panel;
    }

    @Override
    public void mvcGroupInit(final Map<String, Object> args) {
        execSync(new Runnable() {
            public void run() {
                Container container = (Container) getApp().createApplicationContainer();
                if(container instanceof Window) {
                   containerPreInit((Window) container);
                }
                container.add(init());
                if(container instanceof Window) {
                   containerPostInit((Window) container);
                }
            }
        });
    }

    private void containerPreInit(Window window) {
        if(window instanceof Frame) ((Frame) window).setTitle("@griffon.project.name@");
        window.setIconImage(getImage("/griffon-icon-48x48.png"));
        // uncomment the following lines if targeting +JDK6
        // window.setIconImages(java.util.Arrays.asList(
        //     getImage("/griffon-icon-48x48.png"),
        //     getImage("/griffon-icon-32x32.png"),
        //     getImage("/griffon-icon-16x16.png")
        // ));
        window.setLocationByPlatform(true);
        window.setPreferredSize(new Dimension(320, 240));
    }

    private void containerPostInit(Window window) {
        window.pack();
        ((SwingGriffonApplication) getApp()).getWindowManager().attach(window);
    }

    private Image getImage(String path) {
        return Toolkit.getDefaultToolkit().getImage(@artifact.name@.class.getResource(path));
    }
}
