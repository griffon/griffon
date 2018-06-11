package \${groupId}

import griffon.core.artifact.GriffonModel
import griffon.transform.FXObservable
import griffon.metadata.ArtifactProviderFor

@ArtifactProviderFor(GriffonModel)
class AppModel {
    @FXObservable String clickCount = "0"
}