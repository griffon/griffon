package ${groupId};

import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;
import griffon.core.artifact.GriffonView;
import griffon.annotations.inject.MVCMember;
import griffon.lanterna.support.LanternaAction;
import griffon.lanterna.widgets.MutableButton;
import org.kordamp.jipsy.ServiceProviderFor;
import org.codehaus.griffon.runtime.lanterna.artifact.AbstractLanternaGriffonView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import griffon.annotations.core.Nonnull;

@ServiceProviderFor(GriffonView.class)
public class AppView extends AbstractLanternaGriffonView {
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