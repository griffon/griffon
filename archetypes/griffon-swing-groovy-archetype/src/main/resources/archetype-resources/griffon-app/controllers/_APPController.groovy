package \${package}

import griffon.core.artifact.GriffonController
import griffon.core.controller.ControllerAction
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import griffon.annotations.core.Nonnull

@ArtifactProviderFor(GriffonController)
class _APPController {
    @MVCMember @Nonnull
    _APPModel model

    @ControllerAction
    void click() {
        model.clickCount++
    }
}