package ${project_package}

import griffon.annotations.core.Nonnull
import griffon.core.artifact.GriffonController
import griffon.core.controller.ControllerAction
import griffon.annotations.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController
import javax.application.threading.Threading

@ArtifactProviderFor(GriffonController)
class ${project_class_name}Controller extends AbstractGriffonController {
    @MVCMember @Nonnull
    ${project_class_name}Model model

    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void click() {
        int count = model.clickCount.toInteger()
        model.clickCount = String.valueOf(count + 1)
    }
}