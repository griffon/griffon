package ${groupId};

import javax.inject.Named;
import griffon.core.mvc.MVCGroup;
import org.codehaus.griffon.runtime.core.mvc.AbstractTypedMVCGroup;
import javax.annotation.Nonnull;

@Named("app")
public class AppMVCGroup extends AbstractTypedMVCGroup<AppModel, AppView, AppController> {
    public AppMVCGroup(@Nonnull MVCGroup delegate) {
        super(delegate);
    }
}