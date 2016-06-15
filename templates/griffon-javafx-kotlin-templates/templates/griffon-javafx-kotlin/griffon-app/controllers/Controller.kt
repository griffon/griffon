package ${project_package}

import griffon.core.artifact.GriffonController
import griffon.metadata.ArtifactProviderFor
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController
import griffon.transform.Threading

@ArtifactProviderFor(GriffonController::class)
class ${project_class_name}Controller : AbstractGriffonController() {
    lateinit var model: ${project_class_name}Model

    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    fun click() {
        val count = Integer.parseInt(model.clickCount)
        model.clickCount = (count + 1).toString()
    }
}