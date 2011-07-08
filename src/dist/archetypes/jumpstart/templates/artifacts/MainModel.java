@artifact.package@import griffon.plugins.i18n.MessageSourceHolder;
import griffon.util.GriffonNameUtils;
import org.codehaus.griffon.runtime.core.AbstractGriffonModel;
import java.util.Map;

public class @artifact.name@ extends AbstractGriffonModel {
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        firePropertyChange("status", this.status, this.status = status);
    }

    public void mvcGroupInit(Map<String, Object> args) {
        setStatus("Welcome to " + GriffonNameUtils.capitalize(MessageSourceHolder.getMessageSource().getMessage("application.title")));
    }
}
