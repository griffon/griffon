package ${project_package}

import griffon.core.artifact.GriffonModel
import griffon.transform.Observable
import griffon.metadata.ArtifactProviderFor

@ArtifactProviderFor(GriffonModel)
class ${project_capitalized_name}Model {
    @Observable int clickCount = 0
}