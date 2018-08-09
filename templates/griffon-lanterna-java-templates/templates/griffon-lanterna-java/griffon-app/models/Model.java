package ${project_package};

import griffon.core.artifact.GriffonModel;
import org.kordamp.jipsy.ServiceProviderFor;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonModel;

@ServiceProviderFor(GriffonModel.class)
public class ${project_class_name}Model extends AbstractGriffonModel {
    private int clickCount = 0;

    public int getClickCount() {
        return clickCount;
    }

    public void setClickCount(int clickCount) {
        firePropertyChange("clickCount", this.clickCount, this.clickCount = clickCount);
    }
}