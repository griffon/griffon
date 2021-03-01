package \${groupId}

import griffon.annotations.core.Nonnull
import griffon.core.artifact.GriffonController
import griffon.core.controller.ControllerAction
import griffon.annotations.inject.MVCMember
import org.kordamp.jipsy.annotations.ServiceProviderFor
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController

@ServiceProviderFor(GriffonController)
class AppController extends AbstractGriffonController {
    @MVCMember @Nonnull
    AppModel model

    @ControllerAction
    void click() {
        model.clickCount++
    }
}