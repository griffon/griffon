package ${package};

import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;
import griffon.core.artifact.GriffonView;
import griffon.inject.MVCMember;
import griffon.lanterna.support.LanternaAction;
import griffon.lanterna.widgets.MutableButton;
import griffon.metadata.ArtifactProviderFor;
import org.codehaus.griffon.runtime.lanterna.artifact.AbstractLanternaGriffonView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import javax.annotation.Nonnull;

@ArtifactProviderFor(GriffonView.class)
public class _APPView extends AbstractLanternaGriffonView {
    private _APPModel model;
    private _APPController controller;

    @MVCMember
    public void setModel(@Nonnull _APPModel model) {
        this.model = model;
    }

    @MVCMember
    public void setController(@Nonnull _APPController controller) {
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