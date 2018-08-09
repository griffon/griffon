package ${groupId};

import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.Window;
import griffon.core.artifact.GriffonView;
import griffon.annotations.inject.MVCMember;
import griffon.lanterna3.support.LanternaAction;
import griffon.lanterna3.widgets.MutableButton;
import org.kordamp.jipsy.ServiceProviderFor;
import org.codehaus.griffon.runtime.lanterna3.artifact.AbstractLanternaGriffonView;

import griffon.annotations.core.Nonnull;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;

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
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));

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