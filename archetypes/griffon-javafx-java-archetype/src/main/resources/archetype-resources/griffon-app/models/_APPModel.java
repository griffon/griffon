package ${package};

import griffon.annotations.core.Nonnull;
import griffon.core.artifact.GriffonModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonModel;
import org.kordamp.jipsy.annotations.ServiceProviderFor;

@ServiceProviderFor(GriffonModel.class)
public class _APPModel extends AbstractGriffonModel {
    private StringProperty clickCount;

    @Nonnull
    public final StringProperty clickCountProperty() {
        if (clickCount == null) {
            clickCount = new SimpleStringProperty(this, "clickCount", "0");
        }
        return clickCount;
    }

    public void setClickCount(String clickCount) {
        clickCountProperty().set(clickCount);
    }

    public String getClickCount() {
        return clickCountProperty().get();
    }
}