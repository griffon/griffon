package ${package};

import griffon.core.artifact.GriffonModel;
import org.codehaus.griffon.runtime.swing.artifact.AbstractSwingGriffonModel;
import org.kordamp.jipsy.annotations.ServiceProviderFor;

@ServiceProviderFor(GriffonModel.class)
public class _APPModel extends AbstractSwingGriffonModel {
    private int clickCount = 0;

    public int getClickCount() {
        return clickCount;
    }

    public void setClickCount(int clickCount) {
        firePropertyChange("clickCount", this.clickCount, this.clickCount = clickCount);
    }
}