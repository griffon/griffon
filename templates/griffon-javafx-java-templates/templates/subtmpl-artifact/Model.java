package ${project_package};

import griffon.core.artifact.GriffonModel;
import griffon.metadata.ArtifactProviderFor;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonModel;

import javax.annotation.Nonnull;

@ArtifactProviderFor(GriffonModel.class)
public class ${project_class_name}Model extends AbstractGriffonModel {
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
        return clickCount == null ? null : clickCountProperty().get();
    }
}