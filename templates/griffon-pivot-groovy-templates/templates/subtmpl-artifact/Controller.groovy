package ${project_package}

import griffon.core.artifact.GriffonController
import griffon.core.controller.ControllerAction
import griffon.annotations.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import javax.application.threading.Threading
import griffon.annotations.core.Nonnull

@ArtifactProviderFor(GriffonController)
class ${project_class_name}Controller {
    @MVCMember @Nonnull
    ${project_class_name}Model model

    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void click() {
        model.clickCount++
    }
}