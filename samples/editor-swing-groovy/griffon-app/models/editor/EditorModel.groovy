package editor

import griffon.core.artifact.GriffonModel
import groovy.beans.Bindable
import griffon.metadata.ArtifactProviderFor

@ArtifactProviderFor(GriffonModel)
class EditorModel {
    String mvcIdentifier

    @Bindable Document document
}
