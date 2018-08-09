package \${groupId}

import griffon.annotations.core.Nonnull
import griffon.core.artifact.GriffonController
import griffon.core.controller.ControllerAction
import griffon.annotations.inject.MVCMember
import org.kordamp.jipsy.ServiceProviderFor
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController
import javax.application.threading.Threading

@ServiceProviderFor(GriffonController)
class AppController extends AbstractGriffonController {
    @MVCMember @Nonnull
    AppModel model

    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void click() {
        model.clickCount++
    }
}