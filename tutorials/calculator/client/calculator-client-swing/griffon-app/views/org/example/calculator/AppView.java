package org.example.calculator;

import griffon.core.artifact.GriffonView;
import griffon.metadata.ArtifactProviderFor;
import org.codehaus.griffon.runtime.swing.artifact.AbstractSwingGriffonView;

import javax.annotation.Nonnull;
import javax.swing.JComponent;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.Collections;

import static java.util.Arrays.asList;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;

@ArtifactProviderFor(GriffonView.class)
public class AppView extends AbstractSwingGriffonView {
    private JFrame window;
    private JComponent container;

    @Nonnull
    public JFrame getWindow() {
        return window;
    }

    @Nonnull
    public JComponent getContainer() {
        return container;
    }

    @Override
    public void initUI() {
        window = (JFrame) getApplication()
            .createApplicationContainer(Collections.<String, Object>emptyMap());
        window.setName("mainWindow");
        window.setTitle(getApplication().getConfiguration().getAsString("application.title"));
        window.setResizable(false);
        window.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        window.setIconImage(getImage("/griffon-icon-48x48.png"));
        window.setIconImages(asList(
            getImage("/griffon-icon-48x48.png"),
            getImage("/griffon-icon-32x32.png"),
            getImage("/griffon-icon-16x16.png")
        ));
        getApplication().getWindowManager().attach("mainWindow", window);

        window.getContentPane().setLayout(new BorderLayout());
        container = (JComponent) window.getContentPane();
    }

    private Image getImage(String path) {
        return Toolkit.getDefaultToolkit().getImage(AppView.class.getResource(path));
    }
}