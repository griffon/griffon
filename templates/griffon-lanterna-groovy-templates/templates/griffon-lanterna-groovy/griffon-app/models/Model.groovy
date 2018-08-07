package ${project_package}

import griffon.core.artifact.GriffonModel
import griffon.transform.Observable
import griffon.metadata.ArtifactProviderFor

@ArtifactProviderFor(GriffonModel)
class ${project_class_name}Model {
    @Observable int clickCount = 0
}