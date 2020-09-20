package ${groupId};

import griffon.core.artifact.GriffonModel;
import org.kordamp.jipsy.ServiceProviderFor;
import org.codehaus.griffon.runtime.swing.artifact.AbstractSwingGriffonModel;

@ServiceProviderFor(GriffonModel.class)
public class AppModel extends AbstractSwingGriffonModel {
    private int clickCount = 0;

    public int getClickCount() {
        return clickCount;
    }

    public void setClickCount(int clickCount) {
        firePropertyChange("clickCount", this.clickCount, this.clickCount = clickCount);
    }
}