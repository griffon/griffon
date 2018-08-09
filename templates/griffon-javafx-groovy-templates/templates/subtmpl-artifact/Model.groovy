package ${project_package}

import griffon.core.artifact.GriffonModel
import griffon.transform.beans.ObserObservable
import griffon.metadata.ArtifactProviderFor
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonModel

@ArtifactProviderFor(GriffonModel)
class ${project_class_name}Model extends AbstractGriffonModel {
    @FXObservable String clickCount = "0"
}