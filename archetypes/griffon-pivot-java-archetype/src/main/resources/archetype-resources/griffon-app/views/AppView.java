package ${groupId};

import griffon.core.artifact.GriffonView;
import griffon.inject.MVCMember;
import griffon.metadata.ArtifactProviderFor;
import griffon.pivot.support.PivotAction;
import org.apache.pivot.serialization.SerializationException;
import org.codehaus.griffon.runtime.pivot.artifact.AbstractPivotGriffonView;
import org.apache.pivot.wtk.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import javax.annotation.Nonnull;

import static java.util.Arrays.asList;

@ArtifactProviderFor(GriffonView.class)
public class AppView extends AbstractPivotGriffonView {
    private AppModel model;
    private AppController controller;

    @MVCMember
    public void setModel(@Nonnull AppModel model) {
        this.model = model;
    }

    @MVCMember
    public void setController(@Nonnull AppController controller) {
        this.controller = controller;
    }

    @Override
    public void initUI() {
        Window window = (Window) getApplication()
            .createApplicationContainer(Collections.<String, Object>emptyMap());
        window.setTitle(getApplication().getConfiguration().getAsString("application.title"));
        window.setMaximized(true);
        getApplication().getWindowManager().attach("mainWindow", window);

        BoxPane vbox = new BoxPane(Orientation.VERTICAL);
        try {
            vbox.setStyles("{horizontalAlignment:'center', verticalAlignment:'center'}");
        } catch (SerializationException e) {
            // ignore
        }

        final Label clickLabel = new Label(String.valueOf(model.getClickCount()));
        vbox.add(clickLabel);

        PivotAction clickAction = toolkitActionFor(controller, "click");
        final Button button = new PushButton(clickAction.getName());
        button.setName("clickButton");
        button.setAction(clickAction);
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