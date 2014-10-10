package ${project_package};

import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;
import griffon.core.artifact.GriffonView;
import griffon.lanterna.support.LanternaAction;
import griffon.lanterna.widgets.MutableButton;
import griffon.metadata.ArtifactProviderFor;
import org.codehaus.griffon.runtime.lanterna.artifact.AbstractLanternaGriffonView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;

@ArtifactProviderFor(GriffonView.class)
public class ${project_class_name}View extends AbstractLanternaGriffonView {
    private ${project_class_name}Model model;
    private ${project_class_name}Controller controller;

    public void setModel(${project_class_name}Model model) {
        this.model = model;
    }

    public void setController(${project_class_name}Controller controller) {
        this.controller = controller;
    }

    @Override
    public void initUI() {
        Window window = (Window) getApplication()
            .createApplicationContainer(Collections.<String, Object>emptyMap());
        getApplication().getWindowManager().attach("mainWindow", window);
        Panel panel = new Panel(Panel.Orientation.VERTICAL);

        final Label clickLabel = new Label(String.valueOf(model.getClickCount()));
        panel.addComponent(clickLabel);

        LanternaAction clickAction = toolkitActionFor(controller, "click");
        panel.addComponent(new MutableButton(clickAction));

        model.addPropertyChangeListener("clickCount", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                clickLabel.setText(String.valueOf(evt.getNewValue()));
            }
        });

        window.addComponent(panel);
    }
}