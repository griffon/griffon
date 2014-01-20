package ${project_package};

import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonView;
import griffon.metadata.ArtifactProviderFor;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonView;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;

import static java.util.Arrays.asList;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;

@ArtifactProviderFor(GriffonView.class)
public class ${project_capitalized_name}View extends AbstractGriffonView {
    private ${project_capitalized_name}Model model;
    private ${project_capitalized_name}Controller controller;

    @Inject
    public ${project_capitalized_name}View(@Nonnull GriffonApplication application) {
        super(application);
    }

    public void setModel(${project_capitalized_name}Model model) {
        this.model = model;
    }

    public void setController(${project_capitalized_name}Controller controller) {
        this.controller = controller;
    }

    @Override
    public void initUI() {
        JFrame window = (JFrame) getApplication()
            .createApplicationContainer(Collections.<String,Object>emptyMap());
        window.setName("mainWindow");
        window.setTitle(getApplication().getApplicationConfiguration().getAsString("application.title"));
        window.setSize(320, 120);
        window.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        window.setIconImage(getImage("/griffon-icon-48x48.png"));
        window.setIconImages(asList(
            getImage("/griffon-icon-48x48.png"),
            getImage("/griffon-icon-32x32.png"),
            getImage("/griffon-icon-16x16.png")
        ));
        getApplication().getWindowManager().attach("mainWindow", window);

        window.getContentPane().setLayout(new GridLayout(2, 1));

        final JLabel clickLabel = new JLabel(String.valueOf(model.getClickCount()));
        clickLabel.setHorizontalAlignment(SwingConstants.CENTER);
        model.addPropertyChangeListener("clickCount", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                clickLabel.setText(String.valueOf(evt.getNewValue()));
            }
        });
        window.getContentPane().add(clickLabel);
        Action action = (Action) getApplication().getActionManager()
            .actionFor(controller, "click")
            .getToolkitAction();
        window.getContentPane().add(new JButton(action));
    }

    private Image getImage(String path) {
        return Toolkit.getDefaultToolkit().getImage(${project_capitalized_name}View.class.getResource(path));
    }
}