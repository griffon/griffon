package ${project_package}

import griffon.annotations.core.Nonnull
import griffon.core.artifact.GriffonController
import griffon.core.controller.ControllerAction
import griffon.annotations.inject.MVCMember
import org.kordamp.jipsy.annotations.ServiceProviderFor
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController

@ServiceProviderFor(GriffonController)
class ${project_class_name}Controller extends AbstractGriffonController {
    @MVCMember @Nonnull
    ${project_class_name}Model model

    @ControllerAction
    void click() {
        model.clickCount++
    }
}