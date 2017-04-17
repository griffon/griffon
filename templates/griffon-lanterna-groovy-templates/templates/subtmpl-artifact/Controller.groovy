package ${project_package}

import griffon.core.artifact.GriffonController
import griffon.core.controller.ControllerAction
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import javax.annotation.Nonnull

@ArtifactProviderFor(GriffonController)
class ${project_class_name}Controller {
    @MVCMember @Nonnull
    ${project_class_name}Model model

    @ControllerAction
    void click() {
        model.clickCount++
    }
}