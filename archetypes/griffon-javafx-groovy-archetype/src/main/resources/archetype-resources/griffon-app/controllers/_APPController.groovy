package \${package}

import griffon.annotations.controller.ControllerAction
import griffon.annotations.core.Nonnull
import griffon.annotations.inject.MVCMember
import griffon.annotations.threading.Threading
import griffon.core.artifact.GriffonController
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController
import org.kordamp.jipsy.annotations.ServiceProviderFor

@ServiceProviderFor(GriffonController)
class _APPController extends AbstractGriffonController {
    @MVCMember @Nonnull
    _APPModel model

    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void click() {
        int count = model.clickCount.toInteger()
        model.clickCount = String.valueOf(count + 1)
    }
}