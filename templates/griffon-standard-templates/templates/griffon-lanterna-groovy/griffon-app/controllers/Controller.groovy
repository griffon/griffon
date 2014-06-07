package ${project_package}

import griffon.core.artifact.GriffonController
import griffon.metadata.ArtifactProviderFor

@ArtifactProviderFor(GriffonController)
class ${project_capitalized_name}Controller {
    ${project_capitalized_name}Model model

    void click() {
        model.clickCount++
    }
}