package ${project_package}

import griffon.core.artifact.GriffonModel
import groovy.beans.Bindable
import griffon.metadata.ArtifactProviderFor

@ArtifactProviderFor(GriffonModel)
class ${project_capitalized_name}Model {
    @Bindable int clickCount = 0
}