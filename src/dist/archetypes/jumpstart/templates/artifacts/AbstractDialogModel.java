@artifact.package@import griffon.util.GriffonNameUtils;
import griffon.plugins.i18n.MessageSourceHolder;
import org.codehaus.griffon.runtime.core.AbstractGriffonModel;

import java.util.Map;

public abstract class AbstractDialogModel extends AbstractGriffonModel {
    private String title;
    private int width = 0;
    private int height = 0;
    private boolean resizable = true;
    private boolean modal = true;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        firePropertyChange("title", this.title, this.title = title);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        firePropertyChange("width", this.width, this.width = width);
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        firePropertyChange("height", this.height, this.height = height);
    }

    public boolean isResizable() {
        return resizable;
    }

    public void setResizable(boolean resizable) {
        firePropertyChange("resizable", this.resizable, this.resizable = resizable);
    }

    public boolean isModal() {
        return modal;
    }

    public void setModal(boolean modal) {
        firePropertyChange("modal", this.modal, this.modal = modal);
    }

    protected abstract String getDialogKey();

    protected abstract String getDialogTitle();

    public void mvcGroupInit(Map<String, Object> args) {
        title = GriffonNameUtils.capitalize(MessageSourceHolder.getMessageSource().getMessage("application.dialog." + getDialogKey() + ".title", getDialogTitle()));
    }
}
