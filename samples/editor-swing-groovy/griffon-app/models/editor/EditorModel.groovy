package editor

import griffon.core.artifact.GriffonModel
import griffon.transform.Observable
import griffon.metadata.ArtifactProviderFor

@ArtifactProviderFor(GriffonModel)
class EditorModel {
    String mvcIdentifier

    @Observable Document document
}
