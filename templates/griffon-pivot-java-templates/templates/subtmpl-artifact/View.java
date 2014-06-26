package ${project_package};

import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonView;
import griffon.core.controller.Action;
import griffon.metadata.ArtifactProviderFor;
import griffon.pivot.support.PivotAction;
import org.apache.pivot.serialization.SerializationException;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonView;
import org.apache.pivot.wtk.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;

import static java.util.Arrays.asList;

@ArtifactProviderFor(GriffonView.class)
public class ${project_class_name}View extends AbstractGriffonView {
    private ${project_class_name}Model model;
    private ${project_class_name}Controller controller;

    @Inject
    public ${project_class_name}View(@Nonnull GriffonApplication application) {
        super(application);
    }

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
        window.setTitle(getApplication().getConfiguration().getAsString("application.title"));
        window.setMaximized(true);
        getApplication().getWindowManager().attach("${name}", window);

        BoxPane vbox = new BoxPane(Orientation.VERTICAL);
        try {
            vbox.setStyles("{horizontalAlignment:'center', verticalAlignment:'center'}");
        } catch (SerializationException e) {
            // ignore
        }

        final Label clickLabel = new Label(String.valueOf(model.getClickCount()));
        vbox.add(clickLabel);

        Action clickAction = getApplication().getActionManager()
            .actionFor(controller, "click");
        final Button button = new PushButton(clickAction.getName());
        button.setName("clickButton");
        button.setAction((PivotAction) clickAction.getToolkitAction());
        vbox.add(button);

        model.addPropertyChangeListener("clickCount", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                clickLabel.setText(String.valueOf(evt.getNewValue()));
            }
        });

        window.setContent(vbox);
    }
}