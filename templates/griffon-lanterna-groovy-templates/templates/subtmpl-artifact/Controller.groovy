package ${project_package}

import griffon.core.artifact.GriffonController
import griffon.metadata.ArtifactProviderFor

@ArtifactProviderFor(GriffonController)
class ${project_class_name}Controller {
    ${project_class_name}Model model

    void click() {
        model.clickCount++
    }
}