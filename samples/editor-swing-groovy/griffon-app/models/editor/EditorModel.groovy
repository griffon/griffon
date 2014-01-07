package editor

import griffon.core.artifact.GriffonModel
import groovy.beans.Bindable
import org.codehaus.griffon.core.compile.ArtifactProviderFor

@ArtifactProviderFor(GriffonModel)
class EditorModel {
    String mvcIdentifier

    @Bindable Document document
}
