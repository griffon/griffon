@artifact.package@import java.util.Map;
import java.util.LinkedHashMap;
import griffon.util.ApplicationHolder;
import org.codehaus.griffon.runtime.core.AbstractGriffonAddon;

public class @artifact.name@ extends AbstractGriffonAddon {
    public @artifact.name@() {
        super(ApplicationHolder.getApplication());
    }

    public Map<String, Map<String, Object>> getMvcGroups() {
        Map<String, Map<String, Object>> groups = new LinkedHashMap<String, Map<String, Object>>();
        return groups;
    }
}
