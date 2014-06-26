package ${project_package}

import griffon.core.artifact.GriffonController
import griffon.metadata.ArtifactProviderFor
import griffon.transform.Threading

@ArtifactProviderFor(GriffonController)
class ${project_class_name}Controller {
    ${project_class_name}Model model

    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void click() {
        model.clickCount++
    }
}