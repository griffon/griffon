package \${groupId}

import griffon.core.artifact.GriffonModel
import griffon.transform.beans.Observable
import griffon.metadata.ArtifactProviderFor
import org.codehaus.griffon.runtime.pivot.artifact.AbstractPivotGriffonModel

@ArtifactProviderFor(GriffonModel)
class AppModel extends AbstractPivotGriffonModel {
    @Observable int clickCount = 0
}