package ${project_package}

import griffon.core.artifact.GriffonController
import griffon.core.controller.ControllerAction
import griffon.annotations.inject.MVCMember
import org.kordamp.jipsy.ServiceProviderFor
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController
import griffon.annotations.core.Nonnull
import javax.application.threading.Threading

@ServiceProviderFor(GriffonController::class)
class ${project_class_name}Controller : AbstractGriffonController() {
    @set:[MVCMember Nonnull]
    lateinit var model: ${project_class_name}Model

    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    fun click() {
        val count = Integer.parseInt(model.clickCount)
        model.clickCount = (count + 1).toString()
    }
}