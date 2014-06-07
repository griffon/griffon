package ${project_package};

import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonModel;
import griffon.metadata.ArtifactProviderFor;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonModel;

import javax.annotation.Nonnull;
import javax.inject.Inject;

@ArtifactProviderFor(GriffonModel.class)
public class ${project_capitalized_name}Model extends AbstractGriffonModel {
    private int clickCount = 0;

    @Inject
    public ${project_capitalized_name}Model(@Nonnull GriffonApplication application) {
        super(application);
    }

    public int getClickCount() {
        return clickCount;
    }

    public void setClickCount(int clickCount) {
        firePropertyChange("clickCount", this.clickCount, this.clickCount = clickCount);
    }
}