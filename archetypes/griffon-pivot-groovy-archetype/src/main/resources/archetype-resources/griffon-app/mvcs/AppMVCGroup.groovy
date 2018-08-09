package \${groupId}

import javax.inject.Named
import griffon.core.mvc.MVCGroup
import org.codehaus.griffon.runtime.core.mvc.AbstractTypedMVCGroup
import griffon.annotations.core.Nonnull

@Named('app')
class AppMVCGroup extends AbstractTypedMVCGroup<AppModel, AppView, AppController> {
    AppMVCGroup(@Nonnull MVCGroup delegate) {
        super(delegate)
    }
}