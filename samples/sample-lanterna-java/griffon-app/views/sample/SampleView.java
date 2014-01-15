package sample;

import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.component.TextBox;
import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonView;
import griffon.lanterna.support.LanternaAction;
import griffon.lanterna.widgets.MutableButton;
import org.codehaus.griffon.core.compile.ArtifactProviderFor;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonView;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;

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
        Window window = (Window) getApplication()
            .createApplicationContainer(Collections.<String, Object>emptyMap());
        getApplication().getWindowManager().attach("mainWindow", window);        //<2>
        Panel panel = new Panel(Panel.Orientation.VERTICAL);

        panel.addComponent(new Label(getApplication().getMessageSource().getMessage("name.label")));

        final TextBox input = new TextBox();
        panel.addComponent(input);

        LanternaAction sayHelloAction = (LanternaAction) getApplication().getActionManager()
            .actionFor(controller, "sayHello")
            .getToolkitAction();
        final Runnable runnable = sayHelloAction.getRunnable();
        sayHelloAction.setRunnable(new Runnable() {                              //<3>
            @Override
            public void run() {
                model.setInput(input.getText());
                runnable.run();
            }
        });
        panel.addComponent(new MutableButton(sayHelloAction));                   //<4>

        final Label output = new Label();
        panel.addComponent(output);
        model.addPropertyChangeListener("output", new PropertyChangeListener() { //<3>
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                output.setText(String.valueOf(evt.getNewValue()));
            }
        });

        window.addComponent(panel);
    }
}
