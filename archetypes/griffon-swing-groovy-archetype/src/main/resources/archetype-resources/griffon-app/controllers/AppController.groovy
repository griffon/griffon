package \${groupId}

import griffon.core.artifact.GriffonController
import griffon.core.controller.ControllerAction
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import griffon.annotations.core.Nonnull

@ArtifactProviderFor(GriffonController)
class AppController {
    @MVCMember @Nonnull
    AppModel model

    @ControllerAction
    void click() {
        model.clickCount++
    }
}