package sample;

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
public class SampleView extends AbstractGriffonView {
    private SampleController controller;                                         //<1>
    private SampleModel model;                                                   //<1>

    @Inject
    public SampleView(@Nonnull GriffonApplication application) {
        super(application);
    }

    public void setController(SampleController controller) {
        this.controller = controller;
    }

    public void setModel(SampleModel model) {
        this.model = model;
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
        getApplication().getWindowManager().attach("mainWindow", window);        //<2>

        window.getContentPane().setLayout(new GridLayout(4, 1));
        window.getContentPane().add(
            new JLabel(getApplication().getMessageSource().getMessage("name.label"))
        );
        final JTextField nameField = new JTextField();
        nameField.getDocument().addDocumentListener(new DocumentListener() {     //<3>
            @Override
            public void insertUpdate(DocumentEvent e) {
                model.setInput(nameField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                model.setInput(nameField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                model.setInput(nameField.getText());
            }
        });
        window.getContentPane().add(nameField);

        Action action = (Action) getApplication().getActionManager()             //<4>
            .actionFor(controller, "sayHello")
            .getToolkitAction();
        window.getContentPane().add(new JButton(action));

        final JLabel outputLabel = new JLabel();
        model.addPropertyChangeListener("output", new PropertyChangeListener() { // <3>
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                outputLabel.setText(String.valueOf(evt.getNewValue()));
            }
        });
        window.getContentPane().add(outputLabel);
    }

    private Image getImage(String path) {
        return Toolkit.getDefaultToolkit().getImage(SampleView.class.getResource(path));
    }
}
