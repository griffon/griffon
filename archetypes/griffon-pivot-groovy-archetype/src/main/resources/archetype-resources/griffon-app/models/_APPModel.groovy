package \${package}

import griffon.core.artifact.GriffonModel
import griffon.transform.Observable
import griffon.metadata.ArtifactProviderFor

@ArtifactProviderFor(GriffonModel)
class _APPModel {
    @Observable int clickCount = 0
}