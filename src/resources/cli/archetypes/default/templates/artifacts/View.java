@artifact.package@import java.awt.*;
import javax.swing.*;
import java.util.Map;

import griffon.swing.SwingGriffonApplication;
import griffon.swing.WindowManager;
import org.codehaus.griffon.runtime.core.AbstractGriffonView;

public class @artifact.name@ extends AbstractGriffonView {
    private @artifact.name.plain@Controller controller;
    private @artifact.name.plain@Model model;

    public void setController(@artifact.name.plain@Controller controller) {
        this.controller = controller;
    }

    public void setModel(@artifact.name.plain@Model model) {
        this.model = model;
    }

    @Override
    public void mvcGroupInit(Map<String, Object> args) {
        execSync(new Runnable() {
            public void run() {
                JFrame view = new JFrame("@griffon.project.name@");
                view.setIconImage(getImage("/griffon-icon-48x48.png"));
                // uncomment the following lines if targeting +JDK6
                // view.setIconImages(java.util.Arrays.asList(
                //     getImage("/griffon-icon-48x48.png"),
                //     getImage("/griffon-icon-32x32.png"),
                //     getImage("/griffon-icon-16x16.png")
                // ));
                view.setLocationByPlatform(true);
                view.setPreferredSize(new Dimension(320, 240));
                view.getContentPane().add(init());
                view.pack();
                ((SwingGriffonApplication) getApp()).getWindowManager().attach(view);
            }
        });
    }

    // build the UI
    private JComponent init() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Content Goes Here"), BorderLayout.CENTER);
        return panel;
    }

    private Image getImage(String path) {
        return Toolkit.getDefaultToolkit().getImage(@artifact.name@.class.getResource(path));
    }
}
