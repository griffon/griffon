package \${groupId}

import griffon.core.artifact.GriffonModel
import griffon.transform.beans.Observable
import griffon.metadata.ArtifactProviderFor
import org.codehaus.griffon.runtime.lanterna.artifact.AbstractLanternaGriffonModel

@ArtifactProviderFor(GriffonModel)
class AppModel extends AbstractLanternaGriffonModel {
    @Observable int clickCount = 0
}