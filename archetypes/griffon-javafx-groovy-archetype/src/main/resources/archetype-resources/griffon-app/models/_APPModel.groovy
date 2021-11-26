package \${package}

import griffon.core.artifact.GriffonModel
import griffon.transform.FXObservable
import griffon.metadata.ArtifactProviderFor

@ArtifactProviderFor(GriffonModel)
class _APPModel {
    @FXObservable String clickCount = "0"
}