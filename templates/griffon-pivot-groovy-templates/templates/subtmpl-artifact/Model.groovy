package ${project_package}

import griffon.core.artifact.GriffonModel
import griffon.transform.beans.Observable
import griffon.metadata.ArtifactProviderFor
import org.codehaus.griffon.runtime.pivot.artifact.AbstractPivotGriffonModel

@ArtifactProviderFor(GriffonModel)
class ${project_class_name}Model extends AbstractPivotGriffonModel {
    @Observable int clickCount = 0
}