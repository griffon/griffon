package ${project_package}

import griffon.core.artifact.GriffonModel
import griffon.transform.beans.Observable
import griffon.metadata.ArtifactProviderFor
import org.codehaus.griffon.runtime.lanterna.artifact.AbstractLanternaGriffonModel

@ArtifactProviderFor(GriffonModel)
class ${project_class_name}Model extends AbstractLanternaGriffonModel {
    @Observable int clickCount = 0
}