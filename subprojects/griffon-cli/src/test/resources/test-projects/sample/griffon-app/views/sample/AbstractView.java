package sample;

import griffon.swing.SwingGriffonApplication;
import org.codehaus.griffon.runtime.core.AbstractGriffonView;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public abstract class AbstractView extends AbstractGriffonView {
    private AbstractController controller;
    private AbstractModel model;

    public void setController(AbstractController controller) {
        this.controller = controller;
    }

    public void setModel(AbstractModel model) {
        this.model = model;
    }

    public void mvcGroupInit(Map<String, Object> args) {
        execSync(new Runnable() {
            public void run() {
                JFrame view = new JFrame("sample");
                view.setIconImage(getImage("/griffon-icon-48x48.png"));
                // uncomment the following lines if targeting +JDK6
                // view.setIconImages(java.util.Arrays.asList(
                //     getImage("/griffon-icon-48x48.png"),
                //     getImage("/griffon-icon-32x32.png"),
                //     getImage("/griffon-icon-16x16.png")
                // ));
                view.setLocationByPlatform(true);
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
        return Toolkit.getDefaultToolkit().getImage(AbstractView.class.getResource(path));
    }
}
