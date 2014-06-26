package ${project_package}

import griffon.core.artifact.GriffonModel
import griffon.transform.FXObservable
import griffon.metadata.ArtifactProviderFor

@ArtifactProviderFor(GriffonModel)
class ${project_class_name}Model {
    @FXObservable String clickCount = "0"
}